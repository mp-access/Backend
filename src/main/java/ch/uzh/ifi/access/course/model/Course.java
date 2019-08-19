package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class Course implements IndexedCollection<Assignment> {
    private final String id;

    private String gitHash;
    private String gitURL;
    private String directory;

    private String title;
    private String description;
    private String owner;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<String> assistants;
    private List<String> students;

    private List<Assignment> assignments;

    public Course(String name) {
        this.id = new Utils().getID(name);
        this.assistants = new ArrayList<>();
        this.students = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }

    public void set(Course other) {
        this.title = other.title;
        this.description = other.description;
        this.owner = other.owner;
        this.startDate = other.startDate;
        this.endDate = other.endDate;

        this.assistants = other.assistants;
        this.students = other.students;
    }

    public void set(CourseConfig other) {
        this.title = other.getTitle();
        this.description = other.getDescription();
        this.owner = other.getOwner();
        this.startDate = other.getStartDate();
        this.endDate = other.getEndDate();

        this.assistants = other.getAssistants();
        this.students = other.getStudents();
    }

    public void update(Course other) {
        set(other);
        this.gitHash = other.gitHash;

        this.update(other.getIndexedItems());
    }

    public void addAssignment(Assignment a) {
        a.setCourse(this);
        assignments.add(a);
        assignments.sort(Comparator.comparing(Assignment::getIndex));
    }

    public void addAssignments(Assignment... assignments) {
        for (Assignment a : assignments) {
            addAssignment(a);
        }
    }

    public Optional<Assignment> getAssignmentById(String id) {
        return assignments.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    @JsonIgnore
    @Override
    public List<Assignment> getIndexedItems() {
        return assignments;
    }

    @JsonIgnore
    public List<Exercise> getExercises() {
        return assignments.stream().flatMap(assignment -> assignment.getExercises().stream()).collect(Collectors.toList());
    }
}