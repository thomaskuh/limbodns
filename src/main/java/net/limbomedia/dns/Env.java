package net.limbomedia.dns;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Read, initialize and check environment things.
 */
public class Env {

  /**
   * Check and initialize data directory location using specified directory with
   * fallback to execution directory.
   */
  public static File getDataDirectory() {
    String property = System.getProperty("dir");
    File dirData = (property == null || property.isEmpty()) ? retrieveExecutionDir() : new File(property);

    if (!dirData.exists()) {
      throw new IllegalArgumentException("Data directory doesnt exist: " + property);
    }
    if (!dirData.isDirectory()) {
      throw new IllegalArgumentException("Data directory is not a directory: " + property);
    }

    System.setProperty("envDataDir", dirData.getAbsolutePath());

    return dirData;
  }

  /**
   * Get the real execution directory where the application is located and store
   * in system properties.
   * 
   * @return Real execution directory.
   */
  private static File retrieveExecutionDir() {
    String locationJar = LimboDNS.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String locationJarEncoded = null;
    try {
      locationJarEncoded = URLDecoder.decode(locationJar, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      /* by JVM spec */}

    // Get parent (real execution folder) as file-handle
    return new File(locationJarEncoded).getParentFile();
  }

}
