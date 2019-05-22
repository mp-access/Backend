package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.Model.Assignment;
import ch.uzh.ifi.access.course.Model.Course;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CourseDTO {

    private final UUID id;

    private String title;
    private String description;
    private String owner;
    private Date startDate;
    private Date endDate;
    private List<AssignmentDTO> assignments = new ArrayList<>();

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.owner = course.getOwner();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();

        for (Assignment a : course.getAssignments()) {
            this.assignments.add(new AssignmentDTO(a));
        }
    }

    public CourseDTO() {
        id = UUID.randomUUID();
    }

}
