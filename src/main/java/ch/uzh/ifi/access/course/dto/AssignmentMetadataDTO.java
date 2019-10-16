package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentMetadataDTO implements HasPublishingDate, HasDueDate {
    private final String id;

    private String title;
    private String description;
    private ZonedDateTime publishDate;
    private ZonedDateTime dueDate;

    private boolean isPublished;
    private boolean isPastDueDate;

    private List<BreadCrumb> breadCrumbs;
    private List<ExerciseMetadataDTO> exercises = new ArrayList<>();

    public AssignmentMetadataDTO(Assignment assignment) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.publishDate = assignment.getPublishDate();
        this.dueDate = assignment.getDueDate();
        this.isPublished = assignment.isPublished();
        this.isPastDueDate = assignment.isPastDueDate();
        this.breadCrumbs = assignment.getBreadCrumbs();

        if (assignment.getExercises() != null) {
            for (Exercise e : assignment.getExercises()) {
                this.exercises.add(new ExerciseMetadataDTO(e));
            }
        }
    }
}
