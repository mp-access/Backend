package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.BreadCrumb;
import ch.uzh.ifi.access.course.model.Course;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseMetadataDTO {

    private final String id;

    private String title;
    private String description;
    private String owner;
    private String semester;
    private String gitHash;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    private List<BreadCrumb> breadCrumbs;
    private List<AssignmentMetadataDTO> assignments = new ArrayList<>();

    public CourseMetadataDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.owner = course.getOwner();
        this.semester = course.getSemester();
        this.gitHash = course.getGitHash();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();
        this.breadCrumbs = course.getBreadCrumbs();

        for (Assignment a : course.getAssignments()) {
            this.assignments.add(new AssignmentMetadataDTO(a));
        }
    }
}
