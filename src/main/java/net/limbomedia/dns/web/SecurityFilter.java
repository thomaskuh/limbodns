package net.limbomedia.dns.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.kuhlins.webkit.HttpUtils;
import org.kuhlins.webkit.HttpUtils.UserAndPass;

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
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
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
