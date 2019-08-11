package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Assignment {
    private final String id;
    private int index;

    @JsonIgnore
    private Course course;

    private String title;
    private String description;
    private LocalDateTime publishDate;
    private LocalDateTime dueDate;

    private List<Exercise> exercises;

    public Assignment() {
        this.id = new Utils().getID();
        this.exercises = new ArrayList<>();
    }

    public void set(Assignment other) {
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }

    public void update(Assignment other) {
        set(other);

        // Remove non existing Assignments
        for (Iterator<Exercise> i = exercises.iterator(); i.hasNext();) {
            Exercise a = i.next();
            Exercise b = other.exercises.stream().filter(x -> x.getIndex() == a.getIndex()).findFirst().orElse(null);
            if(b == null){
                i.remove();
            }
        }
        // Update or add Assignments
        for (Iterator<Exercise> i = other.exercises.iterator(); i.hasNext();) {
            Exercise b = i.next();
            Exercise a = exercises.stream().filter(x -> x.getIndex() == b.getIndex()).findFirst().orElse(null);
            if(a != null){
                a.update(b);
            }else {
                exercises.add(b);
            }
        }
        // Sort Assignemnts
        exercises.sort(Comparator.comparing(Exercise::getIndex));

        /*
        int diff = exercises.size() - other.exercises.size();
        int size = exercises.size();
        if (diff > 0) {
            // Deleted Assignment
            for (int i = 0; i < Math.abs(diff); ++i) {
                exercises.remove(size - (i + 1));
            }
        } else if (diff < 0) {
            // Added assignment
            for (int i = 0; i < Math.abs(diff); ++i) {
                Exercise e = new Exercise();
                e.set(other.exercises.get(size + i));
                exercises.add(e);
            }
        }

        for (int i = 0; i < exercises.size(); ++i) {
            if (exercises.get(i).hasChanged(other.exercises.get(i))) {
                exercises.get(i).update(other.exercises.get(i));
            }
        }
        */
    }

    public void addExercise(Exercise ex) {
        exercises.add(ex);
        ex.setAssignment(this);
        exercises.sort(Comparator.comparing(Exercise::getIndex));
    }

    public void addExercises(Exercise... exercises) {
        for (Exercise ex : exercises) {
            addExercise(ex);
        }
    }

    public Optional<Exercise> findExerciseById(String id) {
        return exercises.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public boolean isPastDueDate() {
        return LocalDateTime.now().isAfter(dueDate);
    }
}

