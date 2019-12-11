package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import lombok.Data;

@Data
public class ExerciseMetadataDTO {
    private final String id;

    private String title;
    private String longTile;
    private String gitHash;
    private ExerciseType type;
    private String language;
    private Boolean isGraded;
    private double maxScore;
    private int index;

    public ExerciseMetadataDTO(Exercise exercise) {
        this.id = exercise.getId();
        this.title = exercise.getTitle();
        this.longTile = exercise.getLongTitle();
        this.gitHash = exercise.getGitHash();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
        this.isGraded = exercise.getIsGraded();
        this.maxScore = exercise.getMaxScore();
        this.index = exercise.getIndex();
    }
}
