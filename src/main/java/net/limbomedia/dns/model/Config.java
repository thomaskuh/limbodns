package net.limbomedia.dns.model;

import java.io.Serializable;

public class Config implements Serializable {

	private static final long serialVersionUID = -3564348486798708714L;

	private int portUDP = 53;
	private int portTCP = 53;
	private int portHTTP = 7777;
	private int timeout = 5000; // Read-Write-Timeout in ms for TCP requests.

	private String remoteAddressHeader = "X-Forwarded-For";

	private String password = "admin";

	private boolean logQuery = false;
	private boolean logUpdate = false;

	public int getPortUDP() {
		return portUDP;
	}

	public void setPortUDP(int portUDP) {
		this.portUDP = portUDP;
	}

	public int getPortTCP() {
		return portTCP;
	}

	public void setPortTCP(int portTCP) {
		this.portTCP = portTCP;
	}

	public int getPortHTTP() {
		return portHTTP;
	}

	public void setPortHTTP(int portHTTP) {
		this.portHTTP = portHTTP;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemoteAddressHeader() {
		return remoteAddressHeader;
	}

	public void setRemoteAddressHeader(String remoteAddressHeader) {
		this.remoteAddressHeader = remoteAddressHeader;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setLogQuery(boolean logQuery) {
		this.logQuery = logQuery;
	}

	public boolean isLogQuery() {
		return logQuery;
	}

	public void setLogUpdate(boolean logUpdate) {
		this.logUpdate = logUpdate;
	}

	public boolean isLogUpdate() {
		return logUpdate;
	}

	@Override
	public String toString() {
		return "Config [portUDP=" + portUDP + ", portTCP=" + portTCP + ", portHTTP=" + portHTTP + ", timeout=" + timeout
				+ ", remoteAddressHeader=" + remoteAddressHeader + ", password=" + password + ", logQuery=" + logQuery
				+ ", logUpdate=" + logUpdate + "]";
	}

}
