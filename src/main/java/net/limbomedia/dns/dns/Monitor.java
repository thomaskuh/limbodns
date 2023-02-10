package net.limbomedia.dns.dns;

import java.util.concurrent.ExecutorService;

public abstract class Monitor extends Thread {
	
	protected ExecutorService executorService;
	protected Resolver resolver;
	protected final int port;
	protected final int timeoutMs;
	protected boolean log;

	protected boolean running = true;

	public Monitor(ExecutorService executorService, Resolver resolver, int port, int timeoutMs, boolean log) {
		super();
		this.executorService = executorService;
		this.resolver = resolver;
		this.port = port;
		this.timeoutMs = timeoutMs;
		this.log = log;
	}
	
	public void stopIt() {
		this.running = false;
	}
	
}
