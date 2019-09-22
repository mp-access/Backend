package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

@Data
public class ExerciseMetadataDTO {
    private final String id;

    private String gitHash;
    private ExerciseType type;
    private String language;
    private Boolean isGraded;
    private int maxScore;

    public ExerciseMetadataDTO(Exercise exercise){
        this.id = exercise.getId();
        this.gitHash = exercise.getGitHash();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
        this.isGraded = exercise.getIsGraded();
        this.maxScore = exercise.getMaxScore();
    }

    public ExerciseMetadataDTO(){
        this.id = new Utils().getID();
    }
}
