package net.limbomedia.dns.web;

import java.util.concurrent.Executors;
import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class WebServer {

    private static final Logger L = LogManager.getLogger(WebServer.class);

    private Server server;

    public WebServer(Config config, ZoneManager zoneManager) {
        L.info("Starting webserver on port {}.", config.getPortHTTP());

        // Jetty seems not yet that virtual thread ready because this guide-recommended
        // construction still requires and uses a minimum of 3 real threads in that pool.
        QueuedThreadPool threadPool = new QueuedThreadPool(3);
        threadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPortHTTP());
        server.setConnectors(new Connector[] {connector});

        /*
         * Some strange things about Jetty and unhandled requests: They pass
         * ResourceHandler BUT not for path / cause disabled directory listing leads to
         * a 403 instead. They pass ServletHandler/ServletContextHandler but only if
         * ensureDefaultServlet is disabled.
         */

        // Own servlets
        ServletHandler handlerServlets = new ServletHandler();
        handlerServlets.setEnsureDefaultServlet(false);
        handlerServlets.addFilterWithMapping(new FilterHolder(new SecurityFilter(config.getPassword())), "/admin/*", 0);
        handlerServlets.addServletWithMapping(new ServletHolder(new ServletAdmin(config, zoneManager)), "/admin/*");
        handlerServlets.addServletWithMapping(
                new ServletHolder(new ServletApiSimple(config, zoneManager)), "/api/simple/*");
        handlerServlets.addServletWithMapping(
                new ServletHolder(new ServletApiLego(config, zoneManager)), "/api/lego/*");

        handlerServlets.addServletWithMapping(new ServletHolder(new UpdateServlet(config, zoneManager)), "/update/*");

        ServletContextHandler handlerServletsWithContext = new ServletContextHandler("/", false, false);
        handlerServletsWithContext.setHandler(handlerServlets);

        ResourceFactory resourceFactory = ResourceFactory.of(server);

        // Own web resources (ctx handler is required so / will be forwarded not redirected to
        // /index.html)
        ResourceHandler handlerWeb = new ResourceHandler();
        handlerWeb.setBaseResource(resourceFactory.newClassLoaderResource("/web"));
        handlerWeb.setWelcomeFiles(new String[] {"index.html"});
        ContextHandler ctxHandlerWeb = new ContextHandler("/");
        ctxHandlerWeb.setHandler(handlerWeb);

        // webkit resources
        ResourceHandler handlerWebkit = new ResourceHandler();
        handlerWebkit.setBaseResource(resourceFactory.newClassLoaderResource("/webkit"));

        // Register all those handlers
        server.setHandler(new Handler.Sequence(handlerServletsWithContext, ctxHandlerWeb, handlerWebkit));

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Cannot start webserver. " + e.getMessage(), e);
        }
    }
}
