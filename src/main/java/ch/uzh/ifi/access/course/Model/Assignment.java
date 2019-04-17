package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Assignment {
    public String title;
    public String description;
    public Date publishDate;
    public Date dueDate;

    public List<Exercise> exercises = new ArrayList<>();

    public void set(Assignment other){
        this.title = other.title;
        this.description = other.description;
        this.publishDate = other.publishDate;
        this.dueDate = other.dueDate;
    }
}

