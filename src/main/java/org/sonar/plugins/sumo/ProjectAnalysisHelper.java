package org.sonar.plugins.sumo;

import org.sonar.api.ce.posttask.PostProjectAnalysisTask;

public class ProjectAnalysisHelper {
    private static final String DEFAULT_BRANCH = "master";

    private PostProjectAnalysisTask.ProjectAnalysis analysis;

    public ProjectAnalysisHelper(PostProjectAnalysisTask.ProjectAnalysis analysis){
        this.analysis = analysis;
    }

    public String getBranch(){
        if (analysis.getBranch().isPresent()){
            return analysis.getBranch().get().getName()
                    .orElse(DEFAULT_BRANCH);
        }

        return DEFAULT_BRANCH;
    }

    public String getProjectName(){
        return analysis.getProject().getName();
    }
}
