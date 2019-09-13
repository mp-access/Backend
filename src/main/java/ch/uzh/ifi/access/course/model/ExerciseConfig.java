package ch.uzh.ifi.access.course.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ExerciseConfig {
    @JsonProperty(required=true)
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
        this.maxScore = 1;
        this.executionLimits = CodeExecutionLimits.DEFAULTS;
    }
}
