package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonitorTCP extends Monitor {

    private static final Logger L = LogManager.getLogger(MonitorTCP.class);

    private ServerSocket socket;

    public MonitorTCP(ExecutorService executorService, Resolver resolver, int port, int timeoutMs, boolean log) {
        super(executorService, resolver, port, timeoutMs, log);

        L.info("Starting DNS TCP on port {}.", port);

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Error opening TCP socket on port " + port + ". " + e.getMessage(), e);
        }

        executorService.execute(this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket accept = socket.accept();
                accept.setSoTimeout(timeoutMs);
                executorService.execute(new RunnerTCP(resolver, accept, log));
            } catch (Exception e) {
                L.warn("TCP Socket error: {} -> {}.", e.getClass().getSimpleName(), e.getMessage(), e);
            }
        }

        L.info("Shutting down socket on port {}.", port);
    }
}
