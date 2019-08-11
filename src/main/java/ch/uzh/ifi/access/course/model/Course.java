package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Course {
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
        //int diff = assignments.size() - other.assignments.size();
        //int size = assignments.size();


        /*
        Set<Integer> indices = assignments.stream().map(Assignment::getIndex).collect(Collectors.toSet());

        other.assignments.stream()
                .filter(assignment -> indices.contains(assignment.getIndex()))
                .map(Assignment::update)
                .forEach(assignments::add);

        assignments.sort(Comparator.comparing(Assignment::getIndex));
        */

        // Remove non existing Assignments
        for (Iterator<Assignment> i = assignments.iterator(); i.hasNext();) {
            Assignment a = i.next();
            Assignment b = other.assignments.stream().filter(x -> x.getIndex() == a.getIndex()).findFirst().orElse(null);
            if(b == null){
                i.remove();
            }
        }
        // Update or add Assignments
        for (Iterator<Assignment> i = other.assignments.iterator(); i.hasNext();) {
            Assignment b = i.next();
            Assignment a = assignments.stream().filter(x -> x.getIndex() == b.getIndex()).findFirst().orElse(null);
            if(a != null){
                a.update(b);
            }else {
                assignments.add(b);
            }
        }
        // Sort Assignemnts
        assignments.sort(Comparator.comparing(Assignment::getIndex));

        /*
        if (diff > 0) {
            // Deleted Assignment
            for (int i = 0; i < Math.abs(diff); ++i) {
                assignments.remove(size - (i + 1));
            }
        } else if (diff < 0) {
            // Added assignment
            for (int i = 0; i < Math.abs(diff); ++i) {
                Assignment a = new Assignment();
                a.set(other.assignments.get(size + i));
                assignments.add(a);
            }
        }

        for (int i = 0; i < assignments.size(); ++i) {
            assignments.get(i).update(other.assignments.get(i));
        }

       */
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
}