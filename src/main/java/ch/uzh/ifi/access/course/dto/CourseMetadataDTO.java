package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CourseMetadataDTO {

    private final String id;

    private String title;
    private String description;
    private String owner;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<AssignmentMetadataDTO> assignments = new ArrayList<>();

    public CourseMetadataDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.owner = course.getOwner();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();

        for (Assignment a : course.getAssignments()) {
            this.assignments.add(new AssignmentMetadataDTO(a));
        }
    }

    public CourseMetadataDTO() {
        this.id = new Utils().getID();
    }

}