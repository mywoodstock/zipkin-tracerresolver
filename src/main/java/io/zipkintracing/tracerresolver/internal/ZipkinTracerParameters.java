package io.zipkintracing.tracerresolver.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZipkinTracerParameters {

  public static final String DEFAULT_CONFIGURATION_FILEPATH = "tracer.properties";
  public static final String CONFIGURATION_FILE_KEY = "tracer.configFile";

  private final static Logger logger = Logger.getLogger(ZipkinTracerParameters.class.getName());

  // Private constructor to disable instantiation.
  private ZipkinTracerParameters() { }

  public static Properties loadConfigurationFile() {
    String path = System.getProperty(CONFIGURATION_FILE_KEY);
    if (path == null) {
      path = DEFAULT_CONFIGURATION_FILEPATH;
    }

    Properties props = new Properties();
    File configFile = new File(path);
    if (!configFile.isFile()) {
      return props;
    }

    try {
      FileInputStream stream = new FileInputStream(configFile);
      props.load(stream);
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to read Tracer configuration file " + path);
      logger.log(Level.WARNING, e.toString());
    }

    logger.log(Level.INFO, "Successfully loaded Tracer configuration file " + path);
    return props;
  }

}
