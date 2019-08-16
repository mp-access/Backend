package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends CourseConfig implements IndexedCollection<Assignment> {
    private final String id;

    private String gitHash;
    private String gitURL;
    private String directory;

    private List<Assignment> assignments;

    public Course(String name) {
        super();
        this.id = new Utils().getID(name);

        this.assignments = new ArrayList<>();
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

    @Override
    public List<Assignment> getIndexedItems() {
        return assignments;
    }
}