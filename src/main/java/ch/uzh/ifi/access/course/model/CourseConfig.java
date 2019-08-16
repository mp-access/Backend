package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseConfig {
    protected String title;
    protected String description;
    protected String owner;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;

    protected List<String> assistants;
    protected List<String> students;
}
