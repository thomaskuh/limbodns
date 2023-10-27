package net.limbomedia.dns;

import java.util.Collection;
import java.util.List;
import net.limbomedia.dns.model.UpdateResult;
import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XZone;

public interface ZoneManager {

    public Collection<XZone> zoneGets();

    public XZone zoneGet(String zoneId);

    public XZone zoneCreate(String whoDidIt, XZone body);

    public void zoneDelete(String whoDidIt, String zoneId);

    public XRecord recordGet(String zoneId, String recordId);

    public XRecord recordCreate(String whoDidIt, String zoneId, XRecord body);

    public void recordDelete(String whoDidIt, String zoneId, String recordId);

    public XRecord recordUpdate(String whoDidIt, String zoneId, String recordId, XRecord body);

    public List<UpdateResult> recordDynDNS(String whoDidIt, String recordToken, String value);
}
