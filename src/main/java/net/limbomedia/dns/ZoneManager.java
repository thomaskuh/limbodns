package net.limbomedia.dns;

import java.util.Collection;

import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;

public interface ZoneManager {
	
	public Collection<XZone> getXZones();

	public void zoneDelete(String whoDidIt, String Name) throws NotFoundException, UpdateException, ValidationException;
	public void zoneCreate(String whoDidIt, String name, String nameserver) throws UpdateException, ValidationException;

	public void recordDelete(String whoDidIt, String id) throws NotFoundException, UpdateException, ValidationException;
	public void recordCreate(String whoDidIt, String zoneName, String name, XType type, String value) throws NotFoundException, UpdateException, ValidationException;
	public void recordUpdate(String whoDidIt, String id, String value) throws NotFoundException, ValidationException, UpdateException;
	
}
