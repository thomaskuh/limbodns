package net.limbomedia.dns.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kuhlins.lib.webkit.HttpExceptionizer;

public class ServletGeneric extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected final Logger L = LogManager.getLogger(this.getClass());

    protected ObjectMapper mapper = new ObjectMapper();

    protected Collection<RequestHandler> handlers = new ArrayList<>();

    protected HttpExceptionizer exceptionizer = new HttpExceptionizer();

    public ServletGeneric() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean handeled = false;

        try {
            for (RequestHandler handler : handlers) {
                handeled = handler.handle(req, resp);
                if (handeled) {
                    break;
                }
            }
            if (!handeled) {
                L.debug("No handler found for: {}.", req.getPathInfo());
                resp.sendError(404);
            }
        } catch (Exception e) {
            exceptionizer.handleException(e, resp);
        }
    }

    /**
     * Return request path without the mapped prefix like HttpServletRequest.getPathInfo(),
     * but null-safe because when servlet is mapped to "/something/*" but only "/something"
     * (without trailing slash) has been called, getPathInfo() would return null instead of
     * an empty string.
     * @param req
     * @return
     */
    protected String path(HttpServletRequest req) {
        return req.getPathInfo() == null ? "" : req.getPathInfo();
    }
}
