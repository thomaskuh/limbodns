package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorTCP extends Monitor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MonitorTCP.class);
	
	private ServerSocket socket;
	
	public MonitorTCP(ExecutorService executorService, Resolver resolver, int port, int timeoutMs) {
		super(executorService, resolver, port, timeoutMs);
		
		LOG.info("Starting DNS TCP on port " + port + ".");
		
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException("Error opening TCP socket on port " + port + ". " + e.getMessage(),e);
		}
		
		this.start();
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				Socket accept = socket.accept();
				accept.setSoTimeout(timeoutMs);
				executorService.execute(new RunnerTCP(resolver, accept));
			} catch (Exception e) {
				LOG.warn("TCP Socket error: {} -> {}.", e.getClass().getSimpleName(), e.getMessage(), LOG.isDebugEnabled() ? e : null);
			}
		}

		LOG.info("Shutting down socket on port " + port + ".");
	}
	

	
}
