package net.limbomedia.dns;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.limbomedia.dns.dns.DNServer;
import net.limbomedia.dns.dns.Resolver;
import net.limbomedia.dns.dns.ResolverImpl;
import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.web.WebServer;

public class LimboDNS {
	
	private Logger L = LoggerFactory.getLogger(LimboDNS.class);

	private Persistence persistence;
	
	private ZoneManagerImpl zoneManager;
	private Resolver resolver;
	private DNServer dns;
	private Config config;
	private WebServer webServer;
	
	public static void main(String[] args) {
		new LimboDNS(Env.getDataDirectory());
	}

	public LimboDNS(File dataDirectory) {
		L.info("Starting LimboDNS...");
		
		try {
		  this.persistence = new PersistenceImpl(dataDirectory, Defaults::config, Defaults::zones);
	    this.config = this.persistence.configLoad();
		} catch(IOException | IllegalArgumentException e) {
		  L.error("Failed to start LimboDNS. {}.", e.getMessage(), e);
		  System.exit(-1);
		}

		try {
			zoneManager = new ZoneManagerImpl(this.persistence, config.isLogUpdate());
		} catch (IOException e) {
			L.error("Failed to start LimboDNS. Cannot initialize ZoneManager. {}.", e.getMessage(), e);
			System.exit(-1);
		}

		resolver = new ResolverImpl(zoneManager);
		dns = new DNServer(config, resolver);
		webServer = new WebServer(config, zoneManager);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				L.info("Shutting down LimboDNS...");
				dns.shutdown();
				L.info("Shutdown complete. Bye.");
			}
		});
	}
	
}
