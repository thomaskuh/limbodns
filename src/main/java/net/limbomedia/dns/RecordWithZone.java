package net.limbomedia.dns;

import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XZone;

public class RecordWithZone {
	
	private XZone zone;
	private XRecord record;

	public RecordWithZone(XZone zone, XRecord record) {
		this.zone = zone;
		this.record = record;
	}

	public XZone getZone() {
		return zone;
	}

	public XRecord getRecord() {
		return record;
	}
}
