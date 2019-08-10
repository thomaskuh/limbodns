package net.limbomedia.dns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XType;
import net.limbomedia.dns.model.XZone;

public class Defaults {
  
  public static Config config() {
    return new Config();
  }
  
  public static Collection<XZone> zones() {
    List<XZone> result = new ArrayList<XZone>();
    
    XZone zone = new XZone();
    zone.setName("example.com.");
    zone.setNameserver("ns.example.com.");
    
    XRecord r = new XRecord();
    r.setId(UUID.randomUUID().toString());
    r.setName("@");
    r.setType(XType.A);
    r.setValue("93.184.216.34");
    r.setLastChange(new Date());
    zone.getRecords().add(r);
    
    r = new XRecord();
    r.setId(UUID.randomUUID().toString());
    r.setName("@");
    r.setType(XType.AAAA);
    r.setLastChange(new Date());
    r.setValue("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
    zone.getRecords().add(r);

    r = new XRecord();
    r.setId(UUID.randomUUID().toString());
    r.setName("www");
    r.setType(XType.A);
    r.setLastChange(new Date());
    r.setValue("93.184.216.34");
    zone.getRecords().add(r);
    
    r = new XRecord();
    r.setId(UUID.randomUUID().toString());
    r.setName("www");
    r.setType(XType.AAAA);
    r.setLastChange(new Date());
    r.setValue("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
    zone.getRecords().add(r);
    
    r = new XRecord();
    r.setId(UUID.randomUUID().toString());
    r.setName("alias");
    r.setType(XType.CNAME);
    r.setLastChange(new Date());
    r.setValue("www.example.com.");
    zone.getRecords().add(r);
    
    result.add(zone);
    
    return result;
  }
  
}
