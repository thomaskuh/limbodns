package net.limbomedia.dns.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;
import org.eclipse.jetty.http.HttpHeader;
import org.kuhlins.lib.webkit.HttpUtils;
import org.kuhlins.lib.webkit.HttpUtils.UserAndPass;
import org.kuhlins.lib.webkit.ex.NotFoundException;

public class ServletApiLego extends ServletGeneric {

    private static final long serialVersionUID = 1L;
    private ZoneManager zoneManager;
    private Config config;

    private static Pattern PATTERN = Pattern.compile("^/(?<operation>present|cleanup)$");
    private static String PATTERN_OPERATION = "operation";
    private static String PATTERN_OPERATION_CLEANUP = "cleanup";

    public ServletApiLego(Config config, ZoneManager zoneManager) {
        this.config = config;
        this.zoneManager = zoneManager;
        handlers.add(this::handle);
    }

    private boolean handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN.matcher(path(req));
        if (!"POST".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        String operation = matcher.group(PATTERN_OPERATION);

        String remoteAddress = HttpUtils.remoteAdr(req, config.getRemoteAddressHeader());

        UserAndPass uap = HttpUtils.parseBasicAuth(req.getHeader(HttpHeader.AUTHORIZATION.asString()));
        String uapString = uap == null ? "null" : (uap.getUser() + ":" + uap.getPass());
        if (uap == null
                || !("token".equals(uap.getUser()))
                || uap.getPass() == null
                || uap.getPass().isBlank()) {
            L.warn(
                    "Incoming {} request without or invalid auth header. By: {}. Auth: {}.",
                    operation,
                    remoteAddress,
                    uapString);
            resp.sendError(401);
            return true;
        }

        LegoHttpReq body = null;
        try {
            body = mapper.readValue(req.getInputStream(), LegoHttpReq.class);
        } catch (Exception e) {
            L.warn(
                    "Incoming {} request with illegal body. By: {}, Auth: {}, {} -> {}.",
                    operation,
                    remoteAddress,
                    uapString,
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
            resp.sendError(400);
            return true;
        }

        if (PATTERN_OPERATION_CLEANUP.equals(operation)
                && body != null
                && (body.getValue() == null || body.getValue().isBlank())) {
            // Cleanup might contain no value leading to invalid record, so set a dummy value here.
            body.setValue("none");
        }

        if (body == null
                || body.getFqdn() == null
                || body.getFqdn().isBlank()
                || body.getValue() == null
                || body.getValue().isBlank()) {
            L.warn(
                    "Incoming {} request with incomplete body. By: {}, Auth: {}, Body: {}.",
                    operation,
                    remoteAddress,
                    uapString,
                    body);
            resp.sendError(400);
            return true;
        }

        try {
            zoneManager.recordDynDNS(remoteAddress, uap.getPass(), body.getFqdn(), body.getValue());
        } catch (NotFoundException nfe) {
            resp.sendError(400);
            return true;
        }

        return true;
    }

    private static class LegoHttpReq implements Serializable {

        private static final long serialVersionUID = 1L;

        private String fqdn;
        private String value;

        public String getFqdn() {
            return fqdn;
        }

        public void setFqdn(String fqdn) {
            this.fqdn = fqdn;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "LegoHttpReq [fqdn=" + fqdn + ", value=" + value + "]";
        }
    }
}
