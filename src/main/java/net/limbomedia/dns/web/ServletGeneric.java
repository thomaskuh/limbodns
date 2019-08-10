package net.limbomedia.dns.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuhlins.webkit.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ServletGeneric extends HttpServlet {

  private static final long serialVersionUID = 1L;

  protected final Logger L = LoggerFactory.getLogger(this.getClass());

  protected ObjectMapper mapper = new ObjectMapper();
  
  protected Collection<RequestHandler> handlers = new ArrayList<>();

  public ServletGeneric() {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }
  
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    boolean handeled = false;   

    try {
      for(RequestHandler handler : handlers) {
        handeled = handler.handle(req, resp);
        if(handeled) {
          break;
        }
      }
      if(!handeled) {
        L.debug("No handler found for: {}.", req.getPathInfo());
      }
    } catch (Exception e) {
      HttpUtils.handleException(e, resp);
    }
  }

}
