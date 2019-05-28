package ch.uzh.ifi.access.course.model;

import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class Exercise {
    private final String id;
    @JsonIgnore
    private Assignment assignment;

    private ExerciseType type;
    private String language;

    private String question;

    @JsonIgnore
    private List<VirtualFile> private_files = new ArrayList<>();
    @JsonIgnore
    private List<VirtualFile> solution_files = new ArrayList<>();

    private List<VirtualFile> resource_files = new ArrayList<>();
    private List<VirtualFile> public_files= new ArrayList<>();

    public Exercise(){
        this.id = new Utils().getID();
    }

    public void set(Exercise other){
        this.type = other.type;
        this.language = other.language;
    }

    public Optional<VirtualFile> getFileById(String id){
        //TODO: Also search in private files
        Stream<VirtualFile> files = Stream.concat(Stream.concat(public_files.stream(), resource_files.stream()), solution_files.stream());
        return files.filter(file -> file.getId().equals(id)).findFirst();
    }

    public boolean isPastDueDate(){
        return assignment.isPastDueDate();
    }
}