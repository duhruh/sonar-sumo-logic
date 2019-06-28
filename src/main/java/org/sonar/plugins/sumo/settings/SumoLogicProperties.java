package org.sonar.plugins.sumo.settings;

import java.util.List;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class SumoLogicProperties {

  public static final String ACCESS_ID_KEY = "sonar.sumo.accessId";
  public static final String CATEGORY = "Sumo Logic";
  public static final String ACCESS_KEY_KEY = "sonar.sumo.accessKey";
  public static final String HOST_KEY = "sonar.sumo.host";
  public static final String SUMO_SONAR_LOGIN_KEY = "sonar.sumo.sonarLogin";

  private SumoLogicProperties() {
  }

  public static List<PropertyDefinition> getProperties() {
    return asList(
      PropertyDefinition.builder(ACCESS_ID_KEY)
              .name("Access ID")
              .description("Your access ID")
              .onQualifiers(Qualifiers.PROJECT)
              .defaultValue(String.valueOf(false))
              .category(CATEGORY)
              .type(PropertyType.PASSWORD)
              .build(),
    PropertyDefinition.builder(ACCESS_KEY_KEY)
            .name("Access Key")
            .description("Your access key")
            .onQualifiers(Qualifiers.PROJECT)
            .defaultValue(String.valueOf(false))
            .category(CATEGORY)
            .type(PropertyType.PASSWORD)
            .build(),
    PropertyDefinition.builder(HOST_KEY)
            .name("Host")
            .description("Sumo Endpoint")
            .onQualifiers(Qualifiers.PROJECT)
            .defaultValue(String.valueOf(false))
            .category(CATEGORY)
            .type(PropertyType.STRING)
            .build(),
    PropertyDefinition.builder(SUMO_SONAR_LOGIN_KEY)
            .name("Sonar Login")
            .description("Sonar login for internal requests to metrics")
            .onQualifiers(Qualifiers.PROJECT)
            .defaultValue(String.valueOf(false))
            .category(CATEGORY)
            .type(PropertyType.PASSWORD)
            .build());
  }

}
