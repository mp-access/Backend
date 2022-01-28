package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.BreadCrumb;
import ch.uzh.ifi.access.course.model.Course;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
public class CourseMetadataDTO {

    private final String id;

    private String title;
    private String description;
    private String owner;
    private String semester;
    @EqualsAndHashCode.Exclude
    private String gitHash;
    private String roleName;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    @EqualsAndHashCode.Exclude
    private List<BreadCrumb> breadCrumbs;
    @EqualsAndHashCode.Exclude
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
        this.roleName = course.getRoleName();
    }
}
