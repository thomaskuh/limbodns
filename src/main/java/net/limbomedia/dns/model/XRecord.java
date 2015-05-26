package net.limbomedia.dns.model;

import java.io.Serializable;
import java.util.Date;

public class XRecord implements Serializable {
	
	private static final long serialVersionUID = -3123993023759309739L;
	
	private String id;
	private String name;
	private XType type;
	private String value;
	private Date lastChange;
	
	public Date getLastChange() {
		return lastChange;
	}
	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public XType getType() {
		return type;
	}

	public void setType(XType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "XRecord [id=" + id + ", name=" + name + ", type=" + type
				+ ", value=" + value + "]";
	}

}
