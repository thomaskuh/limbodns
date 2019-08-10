package net.limbomedia.dns;

public enum ErrorMsg {
  
  SYSTEM("Operation failed. Check logs."),
  NOTFOUND_ZONE("Zone not found."),
  NOTFOUND_RECORD("Record not found."),

  
  VALIDATION_RECORD_NAME_EXISTS("Record name invalid. Multiples not allowed for this records type."),
  VALIDATION_RECORD_TOKEN_INVALID("Record token invalid. Rules: Empty for no token, otherwise characters: \"a-zA-Z0-9-\"."),
  VALIDATION_RECORD_TOKEN_NOT_UNIQUE("Record token invalid. Must be unique on server."),
  VALIDATION_RECORD_CNAME_DUPE("Record name invalid. Either one cname or multiple other records allowed with same name."),
  
  VAL_ZONE_NAME_INVALID("Name must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),
  VAL_ZONE_NAME_EXISTS("Name already exists."),
  VAL_NAMESERVER_INVALID("Nameserver must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),
  
  VAL_RECORD_NAME_INVALID("Name must be a relative name with characters: 'a-zA-Z0-9-.@'."),
  VAL_RECORD_TYPE_INVALID("Type must be one of: 'A','AAAA','CNAME'."),
  VAL_RECORD_TOKEN_INVALID("Token only allows characters: 'a-zA-Z0-9-'."),
  VAL_RECORD_VALUE_INVALID("Value invalid."),
  VAL_RECORD_VALUE_A_INVALID("A value must a valid IPv4 address."),
  VAL_RECORD_VALUE_AAAA_INVALID("AAAA value must be a valid IPv6 address."),
  VAL_RECORD_VALUE_CNAME_INVALID("CNAME value must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),
  VAL_RECORD_NAME_CONFLICT("There's either one CNAME or multiple OTHER records allowed with the same name."),
  VAL_RECORD_TOKEN_EXISTS("Token already exists. Must be globally unique.");
  
  private ErrorMsg(String text) {}

}
