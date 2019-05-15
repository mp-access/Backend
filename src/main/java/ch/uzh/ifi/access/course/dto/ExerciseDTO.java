package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.Model.Exercise;
import ch.uzh.ifi.access.course.Model.ExerciseType;
import lombok.Data;

import java.util.UUID;

@Data
public class ExerciseDTO {
    private final UUID id;

    private ExerciseType type;
    private String language;

    private String question;

    public ExerciseDTO(Exercise exercise){
        this.id = exercise.getId();
        this.type = exercise.getType();
        this.language = exercise.getLanguage();
        this.question = exercise.getQuestion();
    }

    public ExerciseDTO(){
        this.id = UUID.randomUUID();
    }
}
