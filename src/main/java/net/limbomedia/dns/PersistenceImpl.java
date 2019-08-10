package net.limbomedia.dns;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.XZone;

public class PersistenceImpl implements Persistence {
  
  private static final Logger L = LoggerFactory.getLogger(PersistenceImpl.class);

  private ObjectMapper mapper = new ObjectMapper();
  
  private File dirData;

  public File fileConfig;
  public File fileZones;
  
  public PersistenceImpl(File dataDirectory, Supplier<Config> supplierDefaultConfig, Supplier<Collection<XZone>> supplierDefaultZones) throws IllegalArgumentException, IOException {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    
    this.dirData = dataDirectory;
    
    L.info("Initialized data directory: {}", dirData.getAbsolutePath());

    // Create file references and initialize defaults if files aren't existing yet.
    fileConfig = new File(dirData, "config.json");
    fileZones = new File(dirData, "zones.json");

    if(!fileConfig.exists()) {
      L.info("No config file found. Initalizing defaults: " + fileConfig.getAbsolutePath());
      configSave(supplierDefaultConfig.get());
    }
    
    if(!fileZones.exists()) {
      L.info("No zones file found. Initalizing defaults: " + fileZones.getAbsolutePath());
      zonesSave(supplierDefaultZones.get());
    }
  }
  
  @Override
  public Config configLoad() throws IOException {
    return mapper.readValue(fileConfig, Config.class);
  }
  
  @Override
  public void configSave(Config config) throws IOException {
    mapper.writeValue(fileConfig, config);
  }
  
  @Override
  public Collection<XZone> zonesLoad() throws IOException {
    return mapper.readValue(fileZones, new TypeReference<Collection<XZone>>() {});
  }
  
  @Override
  public void zonesSave(Collection<XZone> zones) throws IOException {
    mapper.writeValue(fileZones, zones);
  }
  
  
  




}
