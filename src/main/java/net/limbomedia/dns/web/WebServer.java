package net.limbomedia.dns.web;

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
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.VirtualThreadPool;

public class WebServer {

    private static final Logger L = LogManager.getLogger(WebServer.class);

    private Server server;

    public WebServer(Config config, ZoneManager zoneManager) {
        L.info("Starting webserver on port {}.", config.getPortHTTP());

        // Jetty server using named virtual threads via VirtualThreadPool.
        // Check: https://jetty.org/docs/jetty/12/programming-guide/arch/threads.html#thread-pool-virtual-threads
        VirtualThreadPool fredsPool = new VirtualThreadPool();
        fredsPool.setName("j");
        server = new Server(fredsPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPortHTTP());
        server.setConnectors(new Connector[] {connector});

        // API servlets and filters
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

        // Static resources from classpath (own and tk-lib-webkit)
        ResourceFactory resourceFactory = ResourceFactory.of(server);

        ResourceHandler handlerResourcesWebOwn = new ResourceHandler();
        handlerResourcesWebOwn.setBaseResource(resourceFactory.newClassLoaderResource("/web", false));
        handlerResourcesWebOwn.setDirAllowed(false);

        ResourceHandler handlerResourcesWebKit = new ResourceHandler();
        handlerResourcesWebKit.setDirAllowed(false);
        handlerResourcesWebKit.setBaseResource(resourceFactory.newClassLoaderResource("/webkit", false));

        // Register all those handlers
        server.setHandler(
                new Handler.Sequence(handlerServletsWithContext, handlerResourcesWebOwn, handlerResourcesWebKit));

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Cannot start webserver. " + e.getMessage(), e);
        }
    }
}
