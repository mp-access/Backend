package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentConfig implements HasPublishingDate {
    protected String title;
    protected String description;
    protected LocalDateTime publishDate;
    protected LocalDateTime dueDate;

    @Override
    public boolean isPublished() {
        return false;
    }
}
