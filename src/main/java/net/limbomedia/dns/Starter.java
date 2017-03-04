package net.limbomedia.dns;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.limbomedia.dns.dns.DNServer;
import net.limbomedia.dns.dns.Resolver;
import net.limbomedia.dns.dns.ResolverImpl;
import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;
import net.limbomedia.dns.web.WebServer;

public class Starter {
	
	private Logger L = LoggerFactory.getLogger(Starter.class);
	
	private static final String VERSION = "2";
	
	public static final String SYSPROP_DIR = "dir";
	public static File datadir;
	public static File fileConfig;
	public static File fileZones;
	
	private ZoneManagerImpl zoneManager;
	private Resolver resolver;
	private DNServer dns;
	private Config config;
	private WebServer webServer;
	
	public static void main(String[] args) {
		String property = System.getProperty(SYSPROP_DIR);
		if(property == null || property.isEmpty()) {
			datadir = Starter.retrieveExecutionDir();
		}
		else {
			datadir = new File(property);
			if(!datadir.exists()) {
				System.err.println("Specified data directory doesn't exist: " + property);
				System.exit(-1);
			}
			if(!datadir.isDirectory()) {
				System.err.println("Specified data directory is not a directory: " + property);
				System.exit(-1);
			}
		}
		
		System.setProperty("datadir", datadir.getAbsolutePath());
		new Starter();
	}
	
	public Starter() {
		L.info("Starting LimboDNS {}", VERSION);
		L.info("Data directory: {}", datadir.getAbsolutePath());
		
		
		fileConfig = new File(datadir, "config.json");
		fileZones = new File(datadir, "zones.json");
		
		try {
			checkConfigFile();
			checkZoneFile();
		} catch (IOException e) {
			L.error("Cannot create default config or example zone file. " + e.getMessage(),e);
			System.exit(1);
		}
		
		// Load Config:
		try {
			L.info("Loading config: " + fileConfig.getAbsolutePath());
			ObjectMapper m = new ObjectMapper();
			config = m.readValue(fileConfig, Config.class);
		} catch (IOException e) {
			L.error("Error reading config file. " + e.getMessage(),e);
			System.exit(1);
		}
		
		try {
			zoneManager = new ZoneManagerImpl();
		} catch (IOException e) {
			L.error("Cannot initialize ZoneManager. " + e.getMessage(),e);
			System.exit(1);
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
	
	/**
	 * Get the real execution directory where the application is located and
	 * store in system properties.
	 * 
	 * @return Real execution directory.
	 */
	private static File retrieveExecutionDir() {
		String locationJar = Starter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		String locationJarEncoded = null;
		try {
			locationJarEncoded = URLDecoder.decode(locationJar, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Get parent (real execution folder) as file-handle
		File execDir = new File(locationJarEncoded).getParentFile();
		return execDir;
	}
	
	private void checkZoneFile() throws IOException {
		if(!fileZones.exists()) {
			L.info("No zones file found. Creating example zones: " + fileZones.getAbsolutePath());
			
			List<XZone> result = new ArrayList<XZone>();
			
			XZone zone = new XZone();
			zone.setName("example.com.");
			zone.setNameserver("ns.example.com.");
			
			XRecord r = new XRecord();
			r.setId(UUID.randomUUID().toString());
			r.setName("@");
			r.setType(XType.A);
			r.setValue("93.184.216.34");
			r.setLastChange(new Date());
			zone.getRecords().add(r);
			
			r = new XRecord();
			r.setId(UUID.randomUUID().toString());
			r.setName("@");
			r.setType(XType.AAAA);
			r.setLastChange(new Date());
			r.setValue("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
			zone.getRecords().add(r);

			r = new XRecord();
			r.setId(UUID.randomUUID().toString());
			r.setName("www");
			r.setType(XType.A);
			r.setLastChange(new Date());
			r.setValue("93.184.216.34");
			zone.getRecords().add(r);
			
			r = new XRecord();
			r.setId(UUID.randomUUID().toString());
			r.setName("www");
			r.setType(XType.AAAA);
			r.setLastChange(new Date());
			r.setValue("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
			zone.getRecords().add(r);
			
			result.add(zone);
			
			ObjectMapper m = new ObjectMapper();
			m.enable(SerializationFeature.INDENT_OUTPUT);
			m.writeValue(Starter.fileZones, result);
		}
	}
	
	private void checkConfigFile() throws IOException {
		if(!fileConfig.exists()) {
			L.info("No config file found. Creating default config: " + fileConfig.getAbsolutePath());
			Config config = new Config();
			ObjectMapper m = new ObjectMapper();
			m.enable(SerializationFeature.INDENT_OUTPUT);
			m.writeValue(Starter.fileConfig, config);
		}
	}	
}
