package net.limbomedia.dns.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class XZone implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String nameserver;
    private long serial = 1L;

    private List<XRecord> records = new ArrayList<XRecord>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameserver() {
        return nameserver;
    }

    public void setNameserver(String nameserver) {
        this.nameserver = nameserver;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    public void incrementSerial() {
        this.serial++;
    }

    public List<XRecord> getRecords() {
        return records;
    }

    public void setRecords(List<XRecord> records) {
        this.records = records;
        if (records != null) {
            records.forEach(record -> record.setZone(this));
        }
    }

    public void addRecord(XRecord record) {
        this.records.add(record);
        record.setZone(this);
    }

    public void removeRecord(XRecord record) {
        this.records.remove(record);
        record.setZone(null);
    }

    @Override
    public String toString() {
        return "XZone [name=" + name + ", nameserver=" + nameserver + ", serial=" + serial + "]";
    }
}
