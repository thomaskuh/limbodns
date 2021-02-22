package net.limbomedia.dns.model;

import java.io.Serializable;

public class UpdateResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String zone;
	private String record;
	private String type;
	private boolean changed;
	private String value;

	public UpdateResult(String zone, String record, String type, boolean changed, String value) {
		this.zone = zone;
		this.record = record;
		this.type = type;
		this.changed = changed;
		this.value = value;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getZone() {
		return zone;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getRecord() {
		return record;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "UpdateResult [zone=" + zone + ", record=" + record + ", type=" + type + ", changed=" + changed
				+ ", value=" + value + "]";
	}

}
