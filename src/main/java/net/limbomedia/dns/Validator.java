package net.limbomedia.dns;

import java.util.regex.Pattern;

import net.limbomedia.dns.model.XType;

public class Validator {
	
	static Pattern ZONE_NAME = Pattern.compile("[a-zA-Z0-9\\-\\.]*\\.");
	static Pattern RECORD_NAME = Pattern.compile("[a-zA-Z0-9\\-\\.@]*");
	
	public static void validateZoneName(String value) throws ValidationException {
		if(value == null || value.trim().isEmpty()) {
			throw new ValidationException("Zone name cannot be empty.", null);
		}
		
		if(!ZONE_NAME.matcher(value).matches()) {
			throw new ValidationException("Zone name invalid. Allowed characters: \"a-z A-Z 0-9 - .\". Must end with \".\".", null);
		}
	}
	
	public static void validateNameserver(String value) throws ValidationException {
		if(value == null || value.trim().isEmpty()) {
			throw new ValidationException("Nameserver cannot be empty.", null);
		}
		
		if(!ZONE_NAME.matcher(value).matches()) {
			throw new ValidationException("Nameserver invalid. Allowed characters: \"a-z A-Z 0-9 - .\". Must end with \".\".", null);
		}
	}
	
	public static void validateRecordName(String value) throws ValidationException {
		if(value == null || value.trim().isEmpty()) {
			throw new ValidationException("Record name cannot be empty.", null);
		}
		
		if(!RECORD_NAME.matcher(value).matches()) {
			throw new ValidationException("Record name. Allowed characters: \"a-z A-Z 0-9 - . @\".", null);
		}
	}
	
	public static void validateValue(String value) throws ValidationException {
		if(value == null || value.trim().isEmpty()) {
			throw new ValidationException("Value cannot be empty.", null);
		}
	}
	
	public static void validateType(XType value) throws ValidationException {
		if(value == null) {
			throw new ValidationException("Type cannot be empty. Allowed values: A, AAAA.", null);
		}
	}
	
	public static void validateID(String value) throws ValidationException {
		if(value == null || value.trim().isEmpty()) {
			throw new ValidationException("ID cannot be empty.", null);
		}
	}
	
	
}
