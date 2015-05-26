package net.limbomedia.dns.dns;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.limbomedia.dns.model.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DNServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(DNServer.class);
	
	private static final int THREADPOOL_SIZE = 5;
	
	private ThreadPoolExecutor threadPool;
	private MonitorUDP monitorUDP;
	private MonitorTCP monitorTCP;
	private Config config;
	public DNServer(Config config, Resolver resolver) {
		this.config = config;
		threadPool = new ThreadPoolExecutor(THREADPOOL_SIZE, THREADPOOL_SIZE, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		if(0 != config.getPortUDP()) {
			monitorUDP = new MonitorUDP(threadPool, resolver, config.getPortUDP());
		}
		if(0 != config.getPortTCP()) {
			monitorTCP = new MonitorTCP(threadPool, resolver, config.getPortTCP());
		}
	}
	
	public synchronized void shutdown() {
		LOG.info("Shutting down...");
		if(0 != config.getPortUDP()) {
			monitorUDP.stopIt();
		}
		if(0 != config.getPortTCP()) {
			monitorTCP.stopIt();
		}
		
		threadPool.shutdown();
		
		try {
			threadPool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.info("Waited 5 seconds for gracefull pool shutdown. Now forcing shutdown.");
			threadPool.shutdownNow();
		}
		
		
	}
	
}
