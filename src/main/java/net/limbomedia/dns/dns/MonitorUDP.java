package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorUDP extends Monitor {

    private static final Logger L = LogManager.getLogger(MonitorUDP.class);

    private DatagramSocket socket;

    private static final short PACKET_SIZE = 512;

    public MonitorUDP(ExecutorService executorService, Resolver resolver, int port, int timeoutMs, boolean log) {
        super(executorService, resolver, port, timeoutMs, log);

        L.info("Starting DNS UPD on port {}.", port);

        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            String msg = "Error opening UDP socket on port " + port + ". " + e.getMessage();
            L.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        executorService.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
                socket.receive(packet);
                executorService.execute(new RunnerUDP(resolver, socket, packet, log));
            } catch (Exception e) {
                L.warn(
                        "UDP Socket error: {} -> {}.",
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        L.isDebugEnabled() ? e : null);
            }
        }

        L.info("Shutting down TCP socket on port {}.", port);
    }
}
