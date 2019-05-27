package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Course {
    private final String id;

    private String directory;

    private String title;
    private String description;
    private String owner;
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<String> assistants = new ArrayList<>();
    private List<String> students = new ArrayList<>();

    private List<Assignment> assignments = new ArrayList<>();

    public Course(){
        this.id = new Utils().getID();
    }

    public void set(Course other){
        //this.directory = other.directory;
        this.title = other.title;
        this.description = other.description;
        this.owner = other.owner;
        this.startDate = other.startDate;
        this.endDate = other.endDate;

        this.assistants = other.assistants;
        this.students = other.students;
    }

    public Optional<Assignment> getAssignmentById(String id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }
}