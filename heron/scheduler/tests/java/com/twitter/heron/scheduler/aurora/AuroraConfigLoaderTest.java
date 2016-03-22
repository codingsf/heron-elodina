package com.twitter.heron.scheduler.aurora;

import java.util.Properties;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Test;

import com.twitter.heron.scheduler.api.Constants;

public class AuroraConfigLoaderTest {
  private static final Logger LOG = Logger.getLogger(AuroraConfigLoaderTest.class.getName());
  private static final ObjectMapper mapper = new ObjectMapper();

  private void addConfig(StringBuilder builder, String key, String value) {
    builder.append(String.format(" %s=\"%s\"", key, value));
  }

  @Test
  public void testAuroraOverrides() throws Exception {
    String override = "dc/role/environ";
    AuroraConfigLoader configLoader = AuroraConfigLoader.class.newInstance();
    configLoader.properties = new Properties();
    // Disables version check
    configLoader.properties.setProperty(Constants.HERON_RELEASE_PACKAGE_NAME, "test");
    configLoader.applyConfigOverride(override);
    Assert.assertEquals("dc", configLoader.properties.getProperty(Constants.DC));
    Assert.assertEquals("role", configLoader.properties.getProperty(Constants.ROLE));
    Assert.assertEquals("environ", configLoader.properties.getProperty(Constants.ENVIRON));
  }

  @Test
  public void testAuroraOverridesWithDefaultOverrides() throws Exception {
    String override = "dc/role/environ key1=value1 key2=value2";
    AuroraConfigLoader configLoader = AuroraConfigLoader.class.newInstance();
    configLoader.properties = new Properties();
    configLoader.properties.setProperty(Constants.HERON_RELEASE_PACKAGE_NAME, "test");
    configLoader.applyConfigOverride(override);
    Assert.assertEquals("dc", configLoader.properties.getProperty(Constants.DC));
    Assert.assertEquals("role", configLoader.properties.getProperty(Constants.ROLE));
    Assert.assertEquals("environ", configLoader.properties.getProperty(Constants.ENVIRON));
    Assert.assertEquals("value1", configLoader.properties.getProperty("key1"));
    Assert.assertEquals("value2", configLoader.properties.getProperty("key2"));
  }

  @Test
  public void testAuroraRespectRespectHeronVersion() throws Exception {
    StringBuilder override = new StringBuilder("dc/role/environ");

    // Add required heron package defaults
    addConfig(override, Constants.HERON_RELEASE_PACKAGE_NAME, "testPackage");
    addConfig(override, Constants.HERON_RELEASE_PACKAGE_ROLE, "test");
    addConfig(override, Constants.HERON_RELEASE_PACKAGE_VERSION, "live");

    AuroraConfigLoader configLoader = AuroraConfigLoader.class.newInstance();
    configLoader.properties = new Properties();
    configLoader.applyConfigOverride(override.toString());
    // Verify translated package
    Assert.assertEquals("live",
        configLoader.properties.getProperty(Constants.HERON_RELEASE_PACKAGE_VERSION));
  }
}