package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    private List<String> assistants = new ArrayList<>();
    private List<String> students = new ArrayList<>();

    private List<Assignment> assignments = new ArrayList<>();

    public Course() {
        this.id = new Utils().getID();
    }

    public void set(Course other) {
        //this.directory = other.directory;
        //this.gitURL = other.gitURL;
        this.title = other.title;
        this.description = other.description;
        this.owner = other.owner;
        this.startDate = other.startDate;
        this.endDate = other.endDate;

        this.assistants = other.assistants;
        this.students = other.students;
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