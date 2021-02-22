package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorUDP extends Monitor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MonitorUDP.class);
	
	private DatagramSocket socket;
	
	private static final short PACKET_SIZE = 512;
	
	public MonitorUDP(ExecutorService executorService, Resolver resolver, int port, int timeoutMs) {
		super(executorService, resolver, port, timeoutMs);
		
		LOG.info("Starting DNS UPD on port " + port + ".");
		
		try {
			socket = new DatagramSocket(port);
		} catch (IOException e) {
			String msg = "Error opening UDP socket on port " + port + ". " + e.getMessage();
			LOG.error(msg,e);
			throw new RuntimeException(msg,e);
		}
		
		this.start();
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
				socket.receive(packet);
				executorService.execute(new RunnerUDP(resolver, socket, packet));
			} catch (Exception e) {
				LOG.warn("UDP Socket error: {} -> {}.", e.getClass().getSimpleName(), e.getMessage(), LOG.isDebugEnabled() ? e : null);				
			}
		}

		LOG.info("Shutting down TCP socket on port " + port + ".");
	}
	
}
