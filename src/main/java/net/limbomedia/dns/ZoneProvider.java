package net.limbomedia.dns;

import org.xbill.DNS.Name;
import org.xbill.DNS.Zone;

public interface ZoneProvider {

	public Zone zoneGet(Name name);
}
