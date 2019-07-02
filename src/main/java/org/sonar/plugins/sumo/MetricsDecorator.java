package org.sonar.plugins.sumo;

import java.sql.Timestamp;
import java.time.Instant;

public class MetricsDecorator implements SumoPayload{

    private SumoPayload payload;

    private ProjectAnalysisHelper analysis;

    public MetricsDecorator(SumoPayload payload, ProjectAnalysisHelper analysis){
        this.payload = payload;
        this.analysis = analysis;
    }

    private String currentTime(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Instant instant = timestamp.toInstant();

        return instant.toString();
    }

    @Override
    public String toJson() {
        return "{" +
                "\"project\":\"" + analysis.getProjectName() + "\"," +
                "\"branch\":\"" + analysis.getBranch() + "\"," +
                "\"timestamp\":\"" + currentTime() + "\"," +
                "\"payload\":" + payload.toJson() +
                "}";
    }


}
