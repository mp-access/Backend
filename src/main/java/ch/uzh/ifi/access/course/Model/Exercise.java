package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Exercise {
    private final UUID id;

    private ExerciseType type;
    private String language;

    private String question;

    private List<FileContent> private_files = new ArrayList<>();
    private List<FileContent> public_files= new ArrayList<>();
    private List<FileContent> resource_files = new ArrayList<>();
    private List<FileContent> solution_files = new ArrayList<>();

    public Exercise(){
        this.id = UUID.randomUUID();
    }

    public void set(Exercise other){
        this.type = other.type;
        this.language = other.language;
    }

    public UUID getId() {
        return id;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<FileContent> getPrivate_files() {
        return private_files;
    }

    public void setPrivate_files(List<FileContent> private_files) {
        this.private_files = private_files;
    }

    public List<FileContent> getPublic_files() {
        return public_files;
    }

    public void setPublic_files(List<FileContent> public_files) {
        this.public_files = public_files;
    }

    public List<FileContent> getResource_files() {
        return resource_files;
    }

    public void setResource_files(List<FileContent> resource_files) {
        this.resource_files = resource_files;
    }

    public List<FileContent> getSolution_files() {
        return solution_files;
    }

    public void setSolution_files(List<FileContent> solution_files) {
        this.solution_files = solution_files;
    }
}
