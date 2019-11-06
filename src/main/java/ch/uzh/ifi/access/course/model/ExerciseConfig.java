package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ExerciseConfig implements Serializable {

    protected String title;
    protected String longTitle;
    protected ExerciseType type;
    protected String language;
    protected Boolean isGraded;
    protected double maxScore;
    protected int maxSubmits;
    protected String gradingSetup;

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
