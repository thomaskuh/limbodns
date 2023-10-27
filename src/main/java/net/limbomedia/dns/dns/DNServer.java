package net.limbomedia.dns.dns;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.limbomedia.dns.model.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DNServer {

    private static final Logger L = LogManager.getLogger(DNServer.class);

    private ExecutorService executor;
    private MonitorUDP monitorUDP;
    private MonitorTCP monitorTCP;
    private Config config;

    public DNServer(Config config, Resolver resolver) {
        this.config = config;
        executor = Executors.newVirtualThreadPerTaskExecutor();

        if (0 != config.getPortUDP()) {
            monitorUDP =
                    new MonitorUDP(executor, resolver, config.getPortUDP(), config.getTimeout(), config.isLogQuery());
        }
        if (0 != config.getPortTCP()) {
            monitorTCP =
                    new MonitorTCP(executor, resolver, config.getPortTCP(), config.getTimeout(), config.isLogQuery());
        }
    }

    public synchronized void shutdown() {
        L.info("Shutting down...");
        if (0 != config.getPortUDP()) {
            monitorUDP.stopIt();
        }
        if (0 != config.getPortTCP()) {
            monitorTCP.stopIt();
        }

        executor.shutdown();

        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            L.info("Waited 5 seconds for gracefull pool shutdown. Now forcing shutdown.");
            executor.shutdownNow();
        }
    }
}
