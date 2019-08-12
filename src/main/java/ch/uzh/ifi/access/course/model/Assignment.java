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

        // Remove from exercises if not in other.exercises
        exercises.removeIf(a -> other.exercises.stream().noneMatch(b -> b.getIndex() == a.getIndex()));

        // Update or add assignments
        other.exercises.forEach(b -> {
            Optional<Exercise> exercise = exercises.stream().filter(a -> a.getIndex() == b.getIndex()).findFirst();
            exercise.ifPresentOrElse(ex -> ex.update(b), () -> exercises.add(b));
        });

        // Sort exercises
        exercises.sort(Comparator.comparing(Exercise::getIndex));
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

