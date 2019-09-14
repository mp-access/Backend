package ch.uzh.ifi.access.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CourseConfig {
    protected String title;
    protected String description;
    protected String owner;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;

    protected List<String> assistants = List.of();
    protected List<String> students = List.of();

    public CourseConfig(){
        this.description = "";
        this.owner = "";
    }
}
