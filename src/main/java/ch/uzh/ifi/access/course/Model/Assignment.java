package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Assignment {
    private final UUID id;

    private String title;
    private String description;
    private Date publishDate;
    private Date dueDate;

    private List<Exercise> exercises = new ArrayList<>();

    public Assignment(){
        this.id = UUID.randomUUID();
    }

    public void set(Assignment other){
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}

