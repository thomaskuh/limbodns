package net.limbomedia.dns.model;

import java.io.Serializable;

public class Config implements Serializable {

	private static final long serialVersionUID = -3564348486798708714L;
	
	private int portUDP = 53;
	private int portTCP = 53;
	private int portHTTP = 7777;
	
	private String remoteAddressHeader = "";
	
	private String password = "admin";
	
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
	
	
	
}
