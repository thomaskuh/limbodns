package net.limbomedia.dns.web;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.MappedLoginService.KnownUser;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
	
	private static final Logger L = LoggerFactory.getLogger(WebServer.class);
	
	private Server server = new Server();
	
	public WebServer(Config config, ZoneManager zoneManager) {
		L.info("Starting webserver on port " + config.getPortHTTP() + ".");
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPortHTTP());
        server.setConnectors(new Connector[] { connector });
        
        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setBaseResource(Resource.newClassPathResource("/web"));
        
        ContextHandler ctxHandler = new ContextHandler("/");
        ctxHandler.setHandler(resHandler);
        
        ServletContextHandler ctxServlet = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctxServlet.setContextPath("/");
        ctxServlet.addServlet(new ServletHolder(new GuiServlet(zoneManager)),"/api/*");
        ctxServlet.addServlet(new ServletHolder(new UpdateServlet(zoneManager)),"/update/*");
        
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(ctxHandler);
        handlers.addHandler(ctxServlet);

        // Security:
        Credential credential = new Password(config.getPassword());
        Principal principal = new KnownUser("admin",credential);
        
        Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        subject.getPrivateCredentials().add(credential);
        
        DefaultUserIdentity identity = new DefaultUserIdentity(subject, principal, new String[] {"user"});
        
        Map<String, UserIdentity> users = new HashMap<String, UserIdentity>();
        users.put("admin", identity);
        
        
        HashLoginService loginService = new HashLoginService("LimboDNS");
        loginService.setUsers(users);
        server.addBean(loginService); 
        
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        server.setHandler(security);
        
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate( true );
        constraint.setRoles(new String[]{"user"});
        
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec( "/api/*" );
        mapping.setConstraint( constraint );
        
        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        
        security.setHandler(handlers);
        
        try {
    		server.start();
	        // server.join();
		} catch (Exception e) {
			throw new RuntimeException("Cannot start webserver. " + e.getMessage(),e);
		}
	}

}
