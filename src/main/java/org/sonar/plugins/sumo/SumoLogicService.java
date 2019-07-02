package org.sonar.plugins.sumo;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sumologic.client.Credentials;
import com.sumologic.client.SumoLogic;
import com.sumologic.client.SumoLogicClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.sumo.settings.SumoLogicProperties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;


@ComputeEngineSide
public class SumoLogicService {
    private static final Logger LOG = Loggers.get(SumoLogicService.class);
    private static final String SOURCE_NAME_HEADER = "X-Sumo-Name";
    private static final String SOURCE_HOST_HEADER = "X-Sumo-Host";
    private static final String SOURCE_CATEGORY_HEADER = "X-Sumo-Category";
    private static final String DEFAULT_SOURCE_CATEGORY_PREFIX = "SonarQube";

    private SumoLogic sumoLogic;

    private String url;

    public SumoLogicService(Configuration configuration) throws MalformedURLException {
        this(
                configuration.get(SumoLogicProperties.ACCESS_ID_KEY).orElse(""),
                configuration.get(SumoLogicProperties.ACCESS_KEY_KEY).orElse(""),
                configuration.get(SumoLogicProperties.HOST_KEY).orElse("")
        );
    }

    public SumoLogicService(String accessId, String accessKey, String url) throws MalformedURLException {
        Credentials creds = new Credentials(accessId, accessKey);
        SumoLogicClient client = new SumoLogicClient(creds);
        client.setURL(url);
        this.url = url;
        this.sumoLogic = client;
    }

    private String getSourceCategoryHeader(ProjectAnalysisHelper analysis){
        return DEFAULT_SOURCE_CATEGORY_PREFIX +
                "/" +
                analysis.getProjectName() +
                "/" +
                analysis.getBranch();
    }

    private CloseableHttpClient generateHttpClient(){
        return HttpClients.createDefault();
    }

    private HttpPost generatePostRequest(SumoPayload payload, ProjectAnalysisHelper analysis) throws InvalidProtocolBufferException, UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(SOURCE_CATEGORY_HEADER, getSourceCategoryHeader(analysis));
        httpPost.setEntity(new StringEntity(payload.toJson()));
        return httpPost;
    }

    /**
     * Pushes WsMeasures.ComponentWsResponse to Sumo Logic using
     * https://help.sumologic.com/03Send-Data/Sources/02Sources-for-Hosted-Collectors/HTTP-Source/Upload-Data-to-an-HTTP-Source
     * @param payload
     * @param analysis
     */
    public void pushMetrics(SumoPayload payload, ProjectAnalysisHelper analysis) {

        try {
            CloseableHttpClient client = generateHttpClient();

            CloseableHttpResponse response = client.execute(generatePostRequest(payload, analysis));

            LOG.info("Uploaded metrics to Sumo Logic: "+ response.getStatusLine());

            client.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
