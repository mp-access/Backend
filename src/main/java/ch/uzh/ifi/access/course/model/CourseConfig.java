package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CourseConfig implements Serializable {

    protected String title;
    protected String description;
    protected String owner;
    protected String semester;
    protected ZonedDateTime startDate;
    protected ZonedDateTime endDate;

    protected List<String> admins = List.of();
    protected List<String> assistants = List.of();
    protected List<String> students = List.of();

    public CourseConfig() {
        this.description = "";
        this.owner = "";
    }

}