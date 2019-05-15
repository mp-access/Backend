package ch.uzh.ifi.access.course.Model;

import lombok.Data;

import java.util.*;

@Data
public class Course {
    private final UUID id;

    private String directory;

    private String title;
    private String description;
    private String owner;
    private Date startDate;

    private Date endDate;

    private List<String> assistants = new ArrayList<>();
    private List<String> students = new ArrayList<>();

    private List<Assignment> assignments = new ArrayList<>();

    public Course(){
        this.id = UUID.randomUUID();
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

    public Optional<Assignment> getAssignmentById(UUID id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }
}