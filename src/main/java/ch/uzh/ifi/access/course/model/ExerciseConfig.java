package ch.uzh.ifi.access.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ExerciseConfig {
    protected ExerciseType type;
    protected String language;
    protected Boolean isGraded;
    protected int maxScore;
    protected int maxSubmits;

    protected List<String> options;
    protected List<String> solutions;
    protected List<String> hints;

    protected CodeExecutionLimits executionLimits;

    public ExerciseConfig() {
        this.isGraded = true;
        this.maxSubmits = 1;
    }

//    @JsonProperty("executionLimits")
//    private void unpackNested(Map<String, String> json) {
//        this.executionLimits = new CodeExecutionLimits(
//                Long.parseLong(json.get("memory")), Long.parseLong(json.get("cpuCores")),
//                Long.parseLong(json.get("timeout")), Boolean.parseBoolean(json.get("networking")));
//
//    }

}
