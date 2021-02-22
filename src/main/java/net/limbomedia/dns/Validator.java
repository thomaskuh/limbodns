package net.limbomedia.dns;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.kuhlins.webkit.ex.ValidationException;
import org.kuhlins.webkit.ex.model.ErrorDetail;
import org.xbill.DNS.Address;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;

import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;

public class Validator {

	private static final String REGEX_ABSOLUTE_NAME = "[a-zA-Z0-9\\-\\.]{1,255}\\.";
	private static final String REGEX_RELATIVE_NAME = "[a-zA-Z0-9\\-\\.@\\*]{1,255}";
	private static final String REGEX_TOKEN = "[a-zA-Z0-9\\-]{0,100}";

	// For absolute names like: zone name, nameserver, cname value.
	static Pattern PATTERN_ABSOLUTE_NAME = Pattern.compile("^" + REGEX_ABSOLUTE_NAME + "$");
	static Pattern PATTERN_RELATIVE_NAME = Pattern.compile("^" + REGEX_RELATIVE_NAME + "$");
	static Pattern PATTERN_TOKEN = Pattern.compile("^" + REGEX_TOKEN + "$");

	private static boolean isInvalidAbsoluteName(String value) {
		if (value == null || !PATTERN_ABSOLUTE_NAME.matcher(value).matches()) {
			return true;
		}

		Name name;
		try {
			name = new Name(value);
		} catch (TextParseException e) {
			return true;
		}

		return !name.isAbsolute();
	}

	private static boolean isInvalidRelativeName(String value) {
		if (value == null || !PATTERN_RELATIVE_NAME.matcher(value).matches()) {
			return true;
		}

		Name name;
		try {
			name = new Name(value);
		} catch (TextParseException e) {
			return true;
		}
		return name.isAbsolute();
	}

	public static void validateZone(XZone value, Collection<XZone> existingZones) {
		ValidationException ve = new ValidationException();

		if (isInvalidAbsoluteName(value.getName())) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_ZONE_NAME_INVALID.name()));
		}
		if (isInvalidAbsoluteName(value.getNameserver())) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_NAMESERVER_INVALID.name()));
		}
		ve.throwOnDetails();

		if (existingZones.stream().anyMatch(x -> x.getName().equals(value.getName()))) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_ZONE_NAME_EXISTS.name()));
		}
		ve.throwOnDetails();
	}

	public static void validateRecordCreate(XRecord record, XZone targetZone, Collection<XZone> existingZones) {
		ValidationException ve = new ValidationException();

		if (isInvalidRelativeName(record.getName())) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_NAME_INVALID.name()));
		}
		if (record.getType() == null) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_TYPE_INVALID.name()));
		}
		if (record.getToken() != null && !PATTERN_TOKEN.matcher(record.getToken()).matches()) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_TOKEN_INVALID.name()));
		}
		validateRecordValue(ve, record.getType(), record.getValue());
		ve.throwOnDetails();

		validateRecordNameConflicts(ve, record, targetZone);

		if (record.getToken() != null && !record.getToken().isEmpty()) {
			validateRecordTokenConflicts(ve, existingZones, record.getType(), record.getToken());
		}

		ve.throwOnDetails();
	}

	public static void validateRecordUpdate(XRecord record, XRecord targetRecord, Collection<XZone> existingZones) {
		ValidationException ve = new ValidationException();

		if (record.getToken() != null && !PATTERN_TOKEN.matcher(record.getToken()).matches()) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_TOKEN_INVALID.name()));
		}
		validateRecordValue(ve, record.getType(), record.getValue());
		ve.throwOnDetails();

		if (record.getToken() != null && !record.getToken().isEmpty()
				&& !record.getToken().equals(targetRecord.getToken())) {
			validateRecordTokenConflicts(ve, existingZones, targetRecord.getType(), record.getToken());
		}
		ve.throwOnDetails();
	}

	public static void validateRecordValue(XType type, String value) {
		ValidationException ve = new ValidationException();
		validateRecordValue(ve, type, value);
		ve.throwOnDetails();
	}

	private static void validateRecordNameConflicts(ValidationException ve, XRecord record, XZone targetZone) {
		if (XType.CNAME.equals(record.getType())) {
			if (targetZone.getRecords().stream().anyMatch(x -> x.getName().equals(record.getName()))) {
				ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_NAME_CONFLICT.name()));
			}
		} else {
			if (targetZone.getRecords().stream()
					.anyMatch(x -> x.getName().equals(record.getName()) && XType.CNAME.equals(x.getType()))) {
				ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_NAME_CONFLICT.name()));
			}
		}
	}

	private static void validateRecordTokenConflicts(ValidationException ve, Collection<XZone> existingZones,
			XType type, String token) {
		if (existingZones.stream().map(XZone::getRecords).flatMap(List::stream).filter(x -> !type.equals(x.getType()))
				.anyMatch(x -> token.equals(x.getToken()))) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_TOKEN_EXISTS.name()));
		}
	}

	private static void validateRecordValue(ValidationException ve, XType type, String value) {
		if (XType.A.equals(type)) {
			try {
				Address.getByAddress(value, Address.IPv4);
			} catch (UnknownHostException | NullPointerException e) {
				ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_VALUE_A_INVALID.name()));
			}
		} else if (XType.AAAA.equals(type)) {
			try {
				Address.getByAddress(value, Address.IPv6);
			} catch (UnknownHostException | NullPointerException e) {
				ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_VALUE_AAAA_INVALID.name()));
			}
		} else if (XType.CNAME.equals(type)) {
			if (isInvalidAbsoluteName(value)) {
				ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_VALUE_CNAME_INVALID.name()));
			}
		} else if (value == null || value.contains(" ")) {
			ve.withDetail(new ErrorDetail(ErrorMsg.VAL_RECORD_VALUE_INVALID.name()));
		}
	}

}
