package org.sonar.plugins.sumo;

import org.sonar.api.CoreProperties;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.sumo.settings.SumoLogicProperties;
import org.sonarqube.ws.WsMeasures;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.measure.ComponentWsRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@ComputeEngineSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class MetricsLoader {
    private static final Logger LOG = Loggers.get(MetricsLoader.class);

    private final Configuration configuration;

    private SumoLogicService service;

    private static final String DEFAULT_BRANCH = "master";
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";

    private static final List<String> METRICS = Collections.unmodifiableList(
            new ArrayList<String>(){{
                add(CoreMetrics.NCLOC_KEY);
                add(CoreMetrics.COMPLEXITY_KEY);
                add(CoreMetrics.VIOLATIONS_KEY);
            }});

    private static final List<String> ADDITIONAL = Collections.unmodifiableList(
            new ArrayList<String>(){{
                add("metrics");
                add("periods");
            }});

    public MetricsLoader(Configuration configuration, SumoLogicService service) {
        this.configuration = configuration;
        this.service = service;
    }

    private WsClient generateClient(){
        HttpConnector connector = HttpConnector.newBuilder()
                .url(getServerURL())
                .credentials(configuration.get(SumoLogicProperties.SUMO_SONAR_LOGIN_KEY).orElse(""), "")
                .build();
        return WsClientFactories.getLocal().newClient(connector);
    }

    private ComponentWsRequest generateRequest(PostProjectAnalysisTask.ProjectAnalysis analysis){
        ComponentWsRequest request = new ComponentWsRequest();
        request.setBranch(getBranch(analysis));
        request.setComponent(getProjectName(analysis));
        request.setMetricKeys(METRICS);
        request.setAdditionalFields(ADDITIONAL);

        return request;
    }

    private String getBranch(PostProjectAnalysisTask.ProjectAnalysis analysis){
        if (analysis.getBranch().isPresent()){
            return analysis.getBranch().get().getName()
                    .orElse(DEFAULT_BRANCH);
        }

        return DEFAULT_BRANCH;
    }

    private String getServerURL(){
        return configuration.get(CoreProperties.SERVER_BASE_URL)
                .orElse(DEFAULT_SERVER_URL);
    }

    private String getProjectName(PostProjectAnalysisTask.ProjectAnalysis analysis){
        return analysis.getProject().getName();
    }

    public void getProject(PostProjectAnalysisTask.ProjectAnalysis analysis){
        LOG.info("Retrieving metrics for: "+getProjectName(analysis)+"/"+getBranch(analysis));

        WsMeasures.ComponentWsResponse resp = generateClient()
                .measures()
                .component(generateRequest(analysis));

        LOG.debug(resp.toString());

        service.pushMetrics(resp);
    }
}
