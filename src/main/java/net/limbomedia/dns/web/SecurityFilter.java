package net.limbomedia.dns.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.eclipse.jetty.http.HttpHeader;
import org.kuhlins.lib.webkit.HttpUtils;
import org.kuhlins.lib.webkit.HttpUtils.UserAndPass;

public class SecurityFilter implements Filter {

    private String user = "admin";
    private String pass;

    public SecurityFilter(String pass) {
        this.pass = pass;
        if (pass == null || pass.isEmpty()) {
            throw new IllegalArgumentException("Admin password must be specified.");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        UserAndPass uap = HttpUtils.parseBasicAuth(req.getHeader(HttpHeader.AUTHORIZATION.asString()));

        if (uap != null && this.user.equals(uap.getUser()) && this.pass.equals(uap.getPass())) {
            chain.doFilter(request, response);
        } else {
            res.sendError(401);
        }
    }
}
