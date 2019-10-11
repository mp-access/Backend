package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class AssignmentConfig implements HasPublishingDate, HasDueDate, Serializable {
    protected String title;
    protected String description;
    protected ZonedDateTime publishDate;
    protected ZonedDateTime dueDate;

    public AssignmentConfig() {
        this.description = "";
    }
}
