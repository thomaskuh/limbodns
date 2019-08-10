package net.limbomedia.dns.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;

public class WebServer {

  private static final Logger L = LoggerFactory.getLogger(WebServer.class);

  private Server server = new Server();

  public WebServer(Config config, ZoneManager zoneManager) {
    L.info("Starting webserver on port " + config.getPortHTTP() + ".");
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(config.getPortHTTP());
    server.setConnectors(new Connector[] { connector });

    /*
     * Some strange things about Jetty and unhandled requests: They pass
     * ResourceHandler BUT not for path / cause disabled directory listing leads to
     * a 403 instead. They pass ServletHandler/ServletContextHandler but only if
     * ensureDefaultServlet is disabled.
     */

    // Own servlets
    ServletHandler handlerServlets = new ServletHandler();
    handlerServlets.setEnsureDefaultServlet(false);
    handlerServlets.addServletWithMapping(new ServletHolder(new ServletApi(config, zoneManager)), "/api/*");
    handlerServlets.addServletWithMapping(new ServletHolder(new UpdateServlet(config, zoneManager)), "/update/*");
    handlerServlets.addFilterWithMapping(new FilterHolder(new SecurityFilter(config.getPassword())), "/api/*", 0);

    // Own web resources (ctx handler is required so / will be forwarded not redirected to /index.html)
    ResourceHandler handlerWeb = new ResourceHandler();
    handlerWeb.setBaseResource(Resource.newClassPathResource("/web"));
    handlerWeb.setWelcomeFiles(new String[] { "index.html" });
    ContextHandler ctxHandlerWeb = new ContextHandler("/");
    ctxHandlerWeb.setHandler(handlerWeb);
    
    // webkit resources
    ResourceHandler handlerWebkit = new ResourceHandler();
    handlerWebkit.setBaseResource(Resource.newClassPathResource("/webkit"));
        
    HandlerList handlers = new HandlerList();
    handlers.addHandler(handlerServlets);
    handlers.addHandler(ctxHandlerWeb);
    handlers.addHandler(handlerWebkit);
    server.setHandler(handlers);

    try {
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Cannot start webserver. " + e.getMessage(), e);
    }
  }

}
