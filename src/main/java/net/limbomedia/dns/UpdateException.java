package net.limbomedia.dns;

public class UpdateException extends Exception {
	
	private static final long serialVersionUID = 2614943191427722950L;

	public UpdateException(String msg) {
		super(msg);
	}
	
	public UpdateException(String msg, Throwable cause) {
		super(msg,cause);
	}
	
}
