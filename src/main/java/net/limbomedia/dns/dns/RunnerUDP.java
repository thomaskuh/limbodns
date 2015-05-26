package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;

public class RunnerUDP implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(RunnerUDP.class);
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private Resolver resolver;
	
	public RunnerUDP(Resolver resolver, DatagramSocket socket, DatagramPacket packet) {
		super();
		this.resolver = resolver;
		this.socket = socket;
		this.packet = packet;
	}

	@Override
	public void run() {
		try{
			byte[] response = null;

			try {
				Message query = new Message(packet.getData());
				LOG.info("Query: " + ResolverImpl.toString(query.getQuestion()) + " from " + packet.getSocketAddress());
				response = resolver.generateReply(query, packet.getData(), packet.getLength(), null);
				if (response == null) {
					return;
				}
			} catch (IOException e) {
				response = resolver.formerrMessage(packet.getData());
			}

			DatagramPacket outdp = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());

			try {
				socket.send(outdp);
			} catch (IOException e) {
				LOG.error("Error sending UDP response to " + packet.getAddress() + ". " + e, e);
			}

		} catch(Exception e){
			LOG.error("Error processing UDP connection from " + packet.getSocketAddress() + ". " + e.getMessage(),e);
		}
	}

}
