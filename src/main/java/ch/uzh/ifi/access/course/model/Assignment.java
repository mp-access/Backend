package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class Assignment {
    private final String id;
    @JsonIgnore
    private Course course;

    private String title;
    private String description;
    private LocalDateTime publishDate;
    private LocalDateTime dueDate;

    private List<Exercise> exercises = new ArrayList<>();

    public Assignment() {
        this.id = new Utils().getID();
    }

    public void set(Assignment other) {
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }

    public void update(Assignment other) {
        set(other);

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
    }

    public void addExercise(Exercise ex) {
        exercises.add(ex);
        ex.setAssignment(this);
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

    public int getMaxScore() {
        return exercises.stream().mapToInt(e -> e.getMaxScore()).sum();
    }

}

