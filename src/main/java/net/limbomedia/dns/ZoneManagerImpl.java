package net.limbomedia.dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.DClass;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.Zone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ZoneManagerImpl implements ZoneManager, ZoneProvider {
	
	private static final Logger L = LoggerFactory.getLogger(ZoneManagerImpl.class);
	
	private ObjectMapper mapper = new ObjectMapper();

	private Collection<XZone> xzones = new ArrayList<XZone>();

	private Collection<Zone> zones = new ArrayList<Zone>();
	
	private ConcurrentHashMap<Name, Zone> zonesMap = new ConcurrentHashMap<Name, Zone>();
	
	public ZoneManagerImpl() throws IOException {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		// Load zones initially from file
		L.info("Loading zones: " + Starter.fileZones.getAbsolutePath());
		xzones = mapper.readValue(Starter.fileZones, new TypeReference<List<XZone>>() {});
		zones = xToRealZones(xzones);
		remap();
	}
	
	private void remap() {
		zonesMap.clear();
		
		for (Zone zone : zones) {
			zonesMap.put(zone.getOrigin(), zone);
		}
	}
	
	private void saveZones(Collection<XZone> zoneList) throws IOException {
		mapper.writeValue(Starter.fileZones, zoneList);
	}
	
	private Collection<Zone> xToRealZones(Collection<XZone> zoneList) throws IOException {
		Collection<Zone> result = new ArrayList<Zone>();
		
		for (XZone xzone : zoneList) {
			Name nameZone = new Name(xzone.getName());
			Name nameNameserver = new Name(xzone.getNameserver());
			
			// Auto-generate SOA and NS record
			SOARecord recordSOA = new SOARecord(nameZone, DClass.IN, 3600L,nameNameserver, new Name("hostmaster",nameZone),xzone.getSerial(), 86400L, 7200L, 3600000L, 172800L);
			NSRecord recordNS = new NSRecord(nameZone, DClass.IN, 300L,nameNameserver);
			
			Zone z = new Zone(nameZone, new Record[] {recordSOA,recordNS});
			
			// Add "user-records"
			for(XRecord xrec : xzone.getRecords()) {
				Record r = null;
				if(XType.A.equals(xrec.getType())) {
					r = new ARecord(new Name(xrec.getName(),nameZone), DClass.IN, 300L, Address.getByAddress(xrec.getValue()));
				}
				if(XType.AAAA.equals(xrec.getType())) {
					r = new AAAARecord(new Name(xrec.getName(),nameZone), DClass.IN, 300L, Address.getByAddress(xrec.getValue()));
				}
				if(r == null) {
					L.error("Invalid/Unsupported record found: " + xrec);
				}
				else {
					z.addRecord(r);
				}
			}
			
			result.add(z);
		}
		
		return result;	
	}
	
	@Override
	public Collection<XZone> getXZones() {
		return xzones;
	}
	
	@Override
	public Zone getZone(Name name) {
		return zonesMap.get(name);
	}
	
	private Collection<XZone> createWorkingCopy() {
		try {
			byte[] bytes = mapper.writeValueAsBytes(xzones);
			return mapper.readValue(bytes, new TypeReference<List<XZone>>() {});
		} catch(IOException e) {
			L.error("Error on zone copy. " + e.getMessage(), e);
			throw new RuntimeException("Internal Error. " + e.getMessage());
		}
	}
	
	private void saveAndUpdate(Collection<XZone> workingCopy) throws ValidationException, UpdateException {
		// Check for zone consistency by "compiling" them. (Duplicates, IPv4 Adresses in AAAA records,...)
		Collection<Zone> newZones = null;
		try {
			newZones = xToRealZones(workingCopy);
		} catch(Exception e) {
			throw new ValidationException(e.getMessage(),e);
		}
		
		// Update file
		try {
			saveZones(workingCopy);
		} catch (IOException e) {
			throw new UpdateException("Cannot save new zones to file. " + e.getMessage(),e);
		}
		
		// Replace current with modified version to bring changes live
		xzones = workingCopy;
		zones = newZones;
		remap();
	}
	
	private XZone findZone(Collection<XZone> zones, String name) {
		for(XZone xz : zones) {
			if(xz.getName().endsWith(name)) {
				return xz;
			}
		}
		return null;
	}
	
	private RecordWithZone findRecord(Collection<XZone> zones, String id) {
		for(XZone xz : zones) {
			for(XRecord xr : xz.getRecords()) {
				if(xr.getId().equals(id)) {
					return new RecordWithZone(xz, xr);
				}
			}
		}
		return null;
	}
	
	@Override
	public synchronized void zoneCreate(String whoDidIt, String name, String nameserver) throws UpdateException, ValidationException {
		Validator.validateZoneName(name);
		Validator.validateNameserver(nameserver);

		Collection<XZone> workingCopy = createWorkingCopy();
		
		XZone newzone = new XZone();
		newzone.setName(name);
		newzone.setNameserver(nameserver);
		workingCopy.add(newzone);
		
		saveAndUpdate(workingCopy);
		L.info("Zone created by " + whoDidIt + ". Zone: " + newzone);
	}
	
	
	@Override
	public synchronized void zoneDelete(String whoDidIt, String name) throws NotFoundException, UpdateException, ValidationException {
		Validator.validateZoneName(name);
		
		Collection<XZone> workingCopy = createWorkingCopy();
		
		XZone xz = findZone(workingCopy, name);
		if(xz == null) {
			throw new NotFoundException(name);
		}
		
		workingCopy.remove(xz);
		
		saveAndUpdate(workingCopy);
		L.info("Zone deleted by " + whoDidIt + ". Zone: " + name);
	}	
	
	@Override
	public synchronized void recordCreate(String whoDidIt, String zoneName, String name, XType type, String value) throws NotFoundException, UpdateException, ValidationException {
		Validator.validateZoneName(zoneName);
		Validator.validateRecordName(name);
		Validator.validateValue(value);
		Validator.validateType(type);
		
		Collection<XZone> workingCopy = createWorkingCopy();
		
		XZone xz = findZone(workingCopy, zoneName);
		if(xz == null) {
			throw new NotFoundException(zoneName);
		}
		
		XRecord r = new XRecord();
		r.setId(UUID.randomUUID().toString());
		r.setName(name);
		r.setType(type);
		r.setValue(value);
		r.setLastChange(new Date());
		xz.getRecords().add(r);

		xz.incrementSerial();

		saveAndUpdate(workingCopy);
		L.info("Record created by " + whoDidIt + ". Zone: " + xz + ", Record: " + r);
	}
	
	@Override
	public synchronized void recordDelete(String whoDidIt, String id) throws NotFoundException, UpdateException, ValidationException {
		Validator.validateID(id);
		
		Collection<XZone> workingCopy = createWorkingCopy();
		
		RecordWithZone rwz = findRecord(workingCopy, id);
		if(rwz == null) {
			throw new NotFoundException(id);
		}
		
		rwz.getZone().getRecords().remove(rwz.getRecord());
		rwz.getZone().incrementSerial();
		
		saveAndUpdate(workingCopy);
		L.info("Record deleted by " + whoDidIt + ". Zone: " + rwz.getZone() + ", Record: " + rwz.getRecord());
	}	
	
	@Override
	public synchronized void recordUpdate(String whoDidIt, final String id, final String value) throws NotFoundException, ValidationException, UpdateException {
		Validator.validateID(id);
		Validator.validateValue(value);
		
		Collection<XZone> workingCopy = createWorkingCopy();

		RecordWithZone rwz = findRecord(workingCopy, id);
		if(rwz == null) {
			throw new NotFoundException(id);
		}

		// Do nothing when old value equals new one.
		if(rwz.getRecord().getValue().equals(value)) {
			return;
		}
		
		// Save changes, increment zone-serial, store as file and update zones.
		rwz.getRecord().setValue(value);
		rwz.getRecord().setLastChange(new Date());
		rwz.getZone().incrementSerial();
		
		saveAndUpdate(workingCopy);
		L.info("Record updated by " + whoDidIt + ". Zone: " + rwz.getZone().getName() + ", Record: " + rwz.getRecord());
	}
	
	

	






	
	

}
