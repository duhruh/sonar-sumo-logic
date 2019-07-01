package org.sonar.plugins.sumo.hooks;

import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.sumo.MetricsLoader;
import org.sonar.plugins.sumo.ProjectAnalysisHelper;
import org.sonar.plugins.sumo.SumoLogicService;

import java.net.MalformedURLException;


public class DisplayQualityGateStatus implements PostProjectAnalysisTask {
  private static final Logger LOG = Loggers.get(DisplayQualityGateStatus.class);

  private final Configuration configuration;

  public DisplayQualityGateStatus (Configuration configuration){
    this.configuration = configuration;
  }

  @Override
  public void finished(ProjectAnalysis analysis) {
    try {
      SumoLogicService service = new SumoLogicService(configuration);
      MetricsLoader loader = new MetricsLoader(configuration);
      ProjectAnalysisHelper meta = new ProjectAnalysisHelper(analysis);

      MetricsLoader.ComponentResponse metrics = loader.getMetricsFromAnalysis(meta);

      service.pushMetrics(metrics, meta);
    } catch (MalformedURLException e) {
      LOG.error(e.getMessage());
      e.printStackTrace();
    }
  }

}
