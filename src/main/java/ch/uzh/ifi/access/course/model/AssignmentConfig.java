package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentConfig {
    protected String title;
    protected String description;
    protected LocalDateTime publishDate;
    protected LocalDateTime dueDate;
}
