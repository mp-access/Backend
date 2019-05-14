package ch.uzh.ifi.access.course.Model;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    public ExerciseType type;
    public String language;

    public String question;

    public List<FileContent> private_files = new ArrayList<>();
    public List<FileContent> public_files= new ArrayList<>();
    public List<FileContent> resource_files = new ArrayList<>();
    public List<FileContent> solution_files = new ArrayList<>();

    public void set(Exercise other){
        this.type = other.type;
        this.language = other.language;
    }
}
