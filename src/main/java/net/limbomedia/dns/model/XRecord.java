package net.limbomedia.dns.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;

public class XRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private XType type;
    private String value;
    private Date lastChange;
    private Date lastUpdate;
    private String token;
    private Long ttl;

    @JsonIgnore
    private transient XZone zone;

    public XZone getZone() {
        return zone;
    }

    public void setZone(XZone zone) {
        this.zone = zone;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "XRecord [id="
                + id
                + ", token="
                + token
                + ", name="
                + name
                + ", ttl="
                + ttl
                + ", type="
                + type
                + ", value="
                + value
                + "]";
    }
}
