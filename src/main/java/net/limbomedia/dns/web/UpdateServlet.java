package net.limbomedia.dns.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.UpdateResult;
import org.kuhlins.lib.webkit.HttpUtils;

public class UpdateServlet extends ServletGeneric {

    private static final long serialVersionUID = -6612011519927117470L;

    private ZoneManager zoneManager;
    private Config config;

    private static Pattern PATTERN_UPDATE = Pattern.compile("^/([a-zA-Z0-9\\-]+)/?([0-9a-fA-F:\\.]+)?$");

    public UpdateServlet(Config config, ZoneManager zoneManager) {
        this.config = config;
        this.zoneManager = zoneManager;
        handlers.add(this::handleUpdate);
    }

    private boolean handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_UPDATE.matcher(req.getPathInfo());
        if (!matcher.matches()) {
            return false;
        }

        String remoteAddress = HttpUtils.remoteAdr(req, config.getRemoteAddressHeader());

        String token = matcher.group(1);
        String value = matcher.group(2);

        if (value == null || value.isEmpty()) {
            value = remoteAddress;
        }

        List<UpdateResult> result = zoneManager.recordDynDNS(remoteAddress, token, value);
        mapper.writeValue(resp.getOutputStream(), result);

        return true;
    }
}
