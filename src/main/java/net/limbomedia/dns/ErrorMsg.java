package net.limbomedia.dns;

public enum ErrorMsg {
    NOTFOUND_ZONE("notfound.zone", "Zone not found."),
    NOTFOUND_RECORD("notfound.record", "Record not found."),

    VAL_DETAIL_ZONE_NAME_EXISTS("val.detail.zone_name_exists", "Name already exists."),
    VAL_DETAIL_ZONE_NAME_INVALID(
            "val.detail.zone_name_invalid",
            "Name must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),

    VAL_DETAIL_NAMESERVER_INVALID(
            "val.detail.nameserver_invalid",
            "Nameserver must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),

    VAL_DETAIL_RECORD_NAME_INVALID(
            "val.detail.record_name_invalid", "Name must be a relative name with characters: 'a-zA-Z0-9-.@*'."),
    VAL_DETAIL_RECORD_TYPE_INVALID("val.detail.record_type_invalid", "Type must be one of: 'A','AAAA','CNAME'."),

    VAL_DETAIL_RECORD_TOKEN_INVALID("val.detail.record_token_invalid", "Token only allows characters: 'a-zA-Z0-9-'."),
    VAL_DETAIL_RECORD_VALUE_INVALID("val.detail.value_invalid", "Value invalid."),
    VAL_DETAIL_RECORD_VALUE_A_INVALID("val.detail.record_value_a_invalid", "A value must a valid IPv4 address."),
    VAL_DETAIL_RECORD_VALUE_AAAA_INVALID(
            "val.detail.record_value_aaaa_invalid", "AAAA value must be a valid IPv6 address."),
    VAL_DETAIL_RECORD_VALUE_CNAME_INVALID(
            "val.detail.record_value_cname_invalid",
            "CNAME value must be an absolute name ending with '.' and characters: 'a-zA-Z0-9-.'."),
    VAL_DETAIL_RECORD_NAME_CONFLICT(
            "val.detail.record_name_conflict",
            "There's either one CNAME or multiple OTHER records allowed with the same name."),
    VAL_DETAIL_RECORD_TOKEN_EXISTS(
            "val.detail.record_token_exists", "Same token cannot be used for multiple records with different types.");

    private String key;
    private String text;

    private ErrorMsg(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String key() {
        return key;
    }

    public String text() {
        return text;
    }
}
