package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {
    public String title;
    public String description;
    public String owner;
    public Date startDate;
    public Date endDate;

    public List<String> assistants = new ArrayList<>();
    public List<String> students = new ArrayList<>();

    public List<Assignment> assignments = new ArrayList<>();

    public void set(Course other){
        this.title = other.title;
        this.description = other.description;
        this.owner = other.owner;
        this.startDate = other.startDate;
        this.endDate = other.endDate;

        this.assistants = other.assistants;
        this.students = other.students;
    }
}