package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xbill.DNS.Message;

public class RunnerUDP implements Runnable {

    private static final Logger L = LogManager.getLogger(RunnerUDP.class);

    private DatagramSocket socket;
    private DatagramPacket packet;
    private Resolver resolver;
    private boolean log;

    public RunnerUDP(Resolver resolver, DatagramSocket socket, DatagramPacket packet, boolean log) {
        super();
        this.resolver = resolver;
        this.socket = socket;
        this.packet = packet;
        this.log = log;
    }

    @Override
    public void run() {
        try {
            byte[] response = null;

            try {
                Message query = new Message(packet.getData());
                if (log) {
                    L.info("Query: {} from {}", ResolverImpl.toString(query.getQuestion()), packet.getSocketAddress());
                }
                response = resolver.generateReply(query, packet.getData(), packet.getLength(), null);
                if (response == null) {
                    return;
                }
            } catch (IOException e) {
                response = resolver.formerrMessage(packet.getData());
            }

            DatagramPacket outdp = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());

            socket.send(outdp);
        } catch (Exception e) {
            L.warn(
                    "Error processing UDP request from {}:{}. {} -> {}.",
                    packet.getSocketAddress(),
                    packet.getPort(),
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
        }
    }
}
