package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssignmentConfig implements HasPublishingDate, Serializable {
    protected String title;
    protected String description;
    protected LocalDateTime publishDate;
    protected LocalDateTime dueDate;

    public AssignmentConfig(){
        this.description = "";
    }
}
