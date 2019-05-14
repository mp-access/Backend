package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Course {
    private final UUID id;

    private String directory;

    private String title;
    private String description;
    private String owner;
    private Date startDate;

    private Date endDate;

    private List<String> assistants = new ArrayList<>();
    private List<String> students = new ArrayList<>();

    private List<Assignment> assignments = new ArrayList<>();

    public Course(){
        this.id = UUID.randomUUID();
    }

    public void set(Course other){
        //this.directory = other.directory;
        this.title = other.title;
        this.description = other.description;
        this.owner = other.owner;
        this.startDate = other.startDate;
        this.endDate = other.endDate;

        this.assistants = other.assistants;
        this.students = other.students;
    }

    public UUID getId() {
        return id;
    }

    public void setDirectory(String directory){
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<String> getAssistants() {
        return assistants;
    }

    public List<String> getStudents() {
        return students;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setAssistants(List<String> assistants) {
        this.assistants = assistants;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}