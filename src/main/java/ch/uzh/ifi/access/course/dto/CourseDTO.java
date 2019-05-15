package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.Model.Assignment;
import ch.uzh.ifi.access.course.Model.Course;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CourseDTO {
    private final UUID id;

    private String title;
    private String description;
    private String owner;
    private Date startDate;
    private Date endDate;

    private List<UUID> assignmentids = new ArrayList<>();

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.owner = course.getOwner();
        this.startDate = course.getStartDate();
        this.endDate = course.getEndDate();

        for(Assignment a : course.getAssignments()){
            this.assignmentids.add(a.getId());
        }
    }

    public CourseDTO() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<UUID> getAssignmentids() {
        return assignmentids;
    }

    public void setAssignmentids(List<UUID> assignmentids) {
        this.assignmentids = assignmentids;
    }

    public static CourseDTO setFromModel(Course course) {
        return new CourseDTO(course);
    }
}
