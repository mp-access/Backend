package ch.uzh.ifi.access.course.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Exercise {
    public ExerciseType type;
    public String language;

    public String question;

    public List<File> private_files = new ArrayList<>();
    public List<File> public_files= new ArrayList<>();
    public List<File> resource_files = new ArrayList<>();
    public List<File> solution_files = new ArrayList<>();

    public void set(Exercise other){
        this.type = other.type;
        this.language = other.language;
    }
}
