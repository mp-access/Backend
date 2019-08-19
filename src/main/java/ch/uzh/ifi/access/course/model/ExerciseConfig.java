package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

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

    public ExerciseConfig(){
        this.isGraded = true;
        this.maxSubmits = 1;
    }
}
