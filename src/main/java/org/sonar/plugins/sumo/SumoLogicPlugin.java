package org.sonar.plugins.sumo;

import org.sonar.api.Plugin;
import org.sonar.plugins.sumo.hooks.DisplayQualityGateStatus;
import org.sonar.plugins.sumo.settings.SumoLogicProperties;


public class SumoLogicPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtension(DisplayQualityGateStatus.class);
    context
      .addExtensions(SumoLogicProperties.getProperties());
  }
}
