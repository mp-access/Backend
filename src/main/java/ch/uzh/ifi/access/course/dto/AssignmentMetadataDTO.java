package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Exercise;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentMetadataDTO {
    private final String id;

    private String title;
    private String description;
    private LocalDateTime publishDate;
    private LocalDateTime dueDate;

    private List<ExerciseMetadataDTO> exercises = new ArrayList<>();

    public AssignmentMetadataDTO(Assignment assignment) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.publishDate = assignment.getPublishDate();
        this.dueDate = assignment.getDueDate();

        for (Exercise e : assignment.getExercises()) {
            this.exercises.add(new ExerciseMetadataDTO(e));
        }
    }

    public boolean isPublished() {
        return publishDate != null && publishDate.isBefore(LocalDateTime.now());
    }
}
