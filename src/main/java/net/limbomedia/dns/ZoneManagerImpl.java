package net.limbomedia.dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.limbomedia.dns.model.UpdateResult;
import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kuhlins.lib.webkit.ex.NotFoundException;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Zone;

public class ZoneManagerImpl implements ZoneManager, ZoneProvider {

    private static final Logger L = LogManager.getLogger(ZoneManagerImpl.class);

    private Persistence persistence;

    private boolean log;

    /**
     * Internal zone model used for persistance and api.
     */
    private Collection<XZone> zones = new ArrayList<XZone>();

    /**
     * External zone model used to answer requests via dnsjava.
     */
    private Map<Name, Zone> resolveMap = new HashMap<Name, Zone>();

    /**
     * Synchronize write access to data in a virtual thread compatible way.
     */
    private final ReentrantLock lock = new ReentrantLock();

    public ZoneManagerImpl(Persistence persistence, boolean log) throws IOException {
        this.persistence = persistence;
        this.log = log;

        // Load zones initially
        zones = this.persistence.zonesLoad();
        updateResolveMap();
    }

    private void updateResolveMap() {
        Map<Name, Zone> result = new HashMap<Name, Zone>();

        for (XZone xzone : zones) {
            Zone z;
            Name nameZone;

            try {
                nameZone = new Name(xzone.getName());
                Name nameNameserver = new Name(xzone.getNameserver());

                // Auto-generate SOA and NS record
                // 1h ttl, 6h refreh, 2h retry, 25d expire, 1h min
                SOARecord recordSOA = new SOARecord(
                        nameZone,
                        DClass.IN,
                        3600L,
                        nameNameserver,
                        new Name("hostmaster", nameZone),
                        xzone.getSerial(),
                        21600L,
                        7200L,
                        2160000L,
                        3600L);
                NSRecord recordNS = new NSRecord(nameZone, DClass.IN, 300L, nameNameserver);

                z = new Zone(nameZone, new Record[] {recordSOA, recordNS});
            } catch (IOException e) {
                L.warn("Cannot go live with zone {}. {}.", xzone.getName(), e.getMessage(), e);
                continue;
            }

            for (XRecord xrec : xzone.getRecords()) {
                try {
                    Record r = null;
                    long ttl = xrec.getTtl() == null ? 300L : xrec.getTtl();
                    if (XType.A.equals(xrec.getType())) {
                        r = new ARecord(
                                new Name(xrec.getName(), nameZone),
                                DClass.IN,
                                ttl,
                                Address.getByAddress(xrec.getValue()));
                    } else if (XType.AAAA.equals(xrec.getType())) {
                        r = new AAAARecord(
                                new Name(xrec.getName(), nameZone),
                                DClass.IN,
                                ttl,
                                Address.getByAddress(xrec.getValue()));
                    } else if (XType.CNAME.equals(xrec.getType())) {
                        r = new CNAMERecord(
                                new Name(xrec.getName(), nameZone), DClass.IN, ttl, new Name(xrec.getValue()));
                    } else if (XType.TXT.equals(xrec.getType())) {
                        List<String> values = new ArrayList<>();
                        for (int i = 0; i < xrec.getValue().length(); i += 255) {
                            values.add(xrec.getValue()
                                    .substring(i, Math.min(xrec.getValue().length(), i + 255)));
                        }
                        r = new TXTRecord(new Name(xrec.getName(), nameZone), DClass.IN, ttl, values);
                    } else if (XType.MX.equals(xrec.getType())) {
                        r = new MXRecord(
                                new Name(xrec.getName(), nameZone), DClass.IN, ttl, 10, new Name(xrec.getValue()));
                    }

                    if (r == null) {
                        L.warn("Skipping a record in zone {}. Invalid/Unsupported: {}.", xzone.getName(), xrec);
                        continue;
                    } else {
                        z.addRecord(r);
                    }
                } catch (IOException e) {
                    L.warn("Skipping a record in zone {}. {}.", xzone.getName(), e.getMessage(), e);
                    continue;
                }
            }

            result.put(z.getOrigin(), z);
        }
        this.resolveMap = result;
    }

    private void onChange() {
        updateResolveMap();

        try {
            this.persistence.zonesSave(zones);
        } catch (IOException e) {
            L.error("Failed to write zone-configuration to file. {}.", e.getMessage(), e);
        }
    }

    private XZone getZone(String name) throws NotFoundException {
        return zones.stream()
                .filter(x -> x.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException(ErrorMsg.NOTFOUND_ZONE.key()));
    }

    private XRecord getRecord(String recordId) throws NotFoundException {
        return zones.stream()
                .map(XZone::getRecords)
                .flatMap(List::stream)
                .filter(x -> x.getId().equals(recordId))
                .findAny()
                .orElseThrow(() -> new NotFoundException(ErrorMsg.NOTFOUND_RECORD.key()));
    }

    private List<XRecord> getRecordsByToken(String token) {
        return zones.stream()
                .map(XZone::getRecords)
                .flatMap(List::stream)
                .filter(x -> x.getToken() != null && x.getToken().equals(token))
                .collect(Collectors.toList());
    }

    @Override
    public Zone zoneGet(Name name) {
        return resolveMap.get(name);
    }

    @Override
    public Collection<XZone> zoneGets() {
        return zones;
    }

    @Override
    public XZone zoneGet(String zoneId) {
        return getZone(zoneId);
    }

    @Override
    public XZone zoneCreate(String whoDidIt, XZone body) {
        lock.lock();
        try {
            Validator.validateZone(body, zones);

            XZone zone = new XZone();
            zone.setName(body.getName());
            zone.setNameserver(body.getNameserver());
            zones.add(zone);

            onChange();

            L.info("Zone created. By {}, Zone: {}.", whoDidIt, zone);
            return zone;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void zoneDelete(String whoDidIt, String zoneId) {
        lock.lock();
        try {
            XZone zone = getZone(zoneId);
            zones.remove(zone);
            onChange();
            L.info("Zone deleted. By: {}, Zone: {}.", whoDidIt, zone);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public XRecord recordGet(String zoneId, String recordId) {
        return getRecord(recordId);
    }

    @Override
    public XRecord recordCreate(String whoDidIt, String zoneId, XRecord body) {
        lock.lock();
        try {
            XZone zone = getZone(zoneId);

            Validator.validateRecordCreate(body, zone, zones);

            XRecord record = new XRecord();
            record.setId(UUID.randomUUID().toString());
            record.setName(body.getName());
            record.setType(body.getType());
            record.setValue(body.getValue());
            record.setLastChange(new Date());
            record.setToken(body.getToken());
            record.setTtl((body.getTtl() == null || body.getTtl() < 0) ? null : body.getTtl());

            zone.addRecord(record);
            zone.incrementSerial();

            onChange();
            L.info("Record updated. By: {}, Zone: {}, {}.", whoDidIt, zone.getName(), record);
            return record;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void recordDelete(String whoDidIt, String zoneId, String recordId) {
        lock.lock();
        try {
            XRecord record = getRecord(recordId);

            XZone zone = record.getZone();

            zone.incrementSerial();
            zone.removeRecord(record);

            onChange();
            L.info("Record deleted. By: {}, Zone: {}, {}.", whoDidIt, zone.getName(), record);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public XRecord recordUpdate(String whoDidIt, String zoneId, String recordId, XRecord body) {
        lock.lock();
        try {
            XRecord record = getRecord(recordId);

            Validator.validateRecordUpdate(body, record, zones);

            // Save changes, increment zone-serial, store as file and update zones.
            record.setToken(body.getToken());
            record.setTtl((body.getTtl() == null || body.getTtl() < 0) ? null : body.getTtl());
            if (!record.getValue().equals(body.getValue())) {
                record.setValue(body.getValue());
                record.setLastChange(new Date());
            }

            record.getZone().incrementSerial();

            onChange();
            L.info(
                    "Record updated. By: {}, Zone: {}, {}.",
                    whoDidIt,
                    record.getZone().getName(),
                    record);
            return record;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<UpdateResult> recordDynDNS(String whoDidIt, String recordToken, String fqdn, String value) {
        lock.lock();
        try {
            List<XRecord> records = getRecordsByToken(recordToken);
            if (records.isEmpty()) {
                if (log) {
                    L.info(
                            "Value updates failed. No records for token. By: {}, Token: {}, Value: {}, Fqdn: {}.",
                            whoDidIt,
                            recordToken,
                            value,
                            fqdn);
                }

                throw new NotFoundException();
            }

            if (fqdn != null) {
                records = records.stream()
                        .filter(rec -> (rec.getName() + "." + rec.getZone().getName()).equals(fqdn))
                        .collect(Collectors.toList());
                if (records.isEmpty()) {
                    if (log) {
                        L.info(
                                "Value updates failed. No records matching token and fqdn. By: {}, Token: {}, Value: {}, Fqdn: {}.",
                                whoDidIt,
                                recordToken,
                                value,
                                fqdn);
                    }

                    throw new NotFoundException();
                }
            }

            List<UpdateResult> results = new ArrayList<UpdateResult>();

            for (XRecord rec : records) {
                String valueExtracted = value;
                if (XType.A == rec.getType()) {
                    valueExtracted = Arrays.stream(value.split(","))
                            .map(adr -> adr.replaceAll("[ \\[\\]]", ""))
                            .filter(adr -> Address.toArray(adr, Address.IPv4) != null)
                            .findFirst()
                            .orElse(valueExtracted);
                }
                if (XType.AAAA == rec.getType()) {
                    valueExtracted = Arrays.stream(value.split(","))
                            .map(adr -> adr.replaceAll("[ \\[\\]]", ""))
                            .filter(adr -> Address.toArray(adr, Address.IPv6) != null)
                            .findFirst()
                            .orElse(valueExtracted);
                }

                UpdateResult updateResult = recordDynDNS(whoDidIt, rec, valueExtracted);
                results.add(updateResult);
            }

            if (results.stream().anyMatch(UpdateResult::isChanged)) {
                onChange();
            }

            return results;
        } finally {
            lock.unlock();
        }
    }

    private UpdateResult recordDynDNS(String whoDidIt, XRecord record, String value) {
        UpdateResult result = new UpdateResult(
                record.getZone().getName(), record.getName(), record.getType().name(), false, value);

        ErrorMsg errorMsg = Validator.validateRecordValueSimple(record.getType(), value);
        if (errorMsg == null) {
            Date now = new Date();
            record.setLastUpdate(now);

            if (!record.getValue().equals(value)) {
                result.setChanged(true);
                record.setValue(value);
                record.setLastChange(now);
            }
        } else {
            result.setError("Invalid value: " + errorMsg.text());
        }

        if (log) {
            L.info("Value updated. By: {}, {}, Value: {}.", whoDidIt, result, value);
        }

        return result;
    }
}
