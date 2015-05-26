package net.limbomedia.dns;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 5549261449615122190L;
	
	public NotFoundException(String id) {
		super("No zone/record found with identifier " + id + ".");
	}
	
}
