package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

@Data
public class ExerciseMetadataDTO {
    private final String id;

    private ExerciseType type;
    private String language;

    public ExerciseMetadataDTO(Exercise exercise){
        this.id = exercise.getId();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
    }

    public ExerciseMetadataDTO(){
        this.id = new Utils().getID();
    }
}
