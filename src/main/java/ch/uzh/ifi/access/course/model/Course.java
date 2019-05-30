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

    public void update(Course other){
        set(other);
        int diff = assignments.size() - other.assignments.size();
        int size = assignments.size();
        if(diff > 0){
            // Deleted Assignment
            for(int i = 0; i < Math.abs(diff); ++i){
                assignments.remove(size - (i+1));
            }
        }else if(diff < 0){
            // Added assignment
            for(int i = 0; i < Math.abs(diff); ++i){
                Assignment a = new Assignment();
                a.set(other.assignments.get(size + i));
                assignments.add(a);
            }
        }

        for(int i = 0; i < assignments.size(); ++i){
            assignments.get(i).set(other.assignments.get(i));
        }
    }

    public Optional<Assignment> getAssignmentById(String id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }
}