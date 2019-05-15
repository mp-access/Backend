package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.Model.Assignment;
import ch.uzh.ifi.access.course.Model.Exercise;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class AssignmentDTO {
    private final UUID id;

    private String title;
    private String description;
    private Date publishDate;
    private Date dueDate;

    private List<UUID> exerciseids = new ArrayList<>();

    public AssignmentDTO(Assignment assignment){
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.publishDate = assignment.getPublishDate();
        this.dueDate = assignment.getDueDate();

        for(Exercise e : assignment.getExercises()){
            this.exerciseids.add(e.getId());
        }
    }

    public AssignmentDTO(){
        id = UUID.randomUUID();
    }
}
