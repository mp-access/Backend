package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseConfig {
    private String title;
    private String description;
    private String owner;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<String> assistants;
    private List<String> students;
}
