package net.limbomedia.dns;

import java.io.IOException;
import java.util.Collection;

import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.XZone;

public interface Persistence {
  
  public Config configLoad() throws IOException;
  public void configSave(Config config) throws IOException;
  public Collection<XZone> zonesLoad() throws IOException;
  public void zonesSave(Collection<XZone> zones) throws IOException;
  
}
