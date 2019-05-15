package ch.uzh.ifi.access.course.Model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Data
public class Exercise {
    private final UUID id;

    private ExerciseType type;
    private String language;

    private String question;

    private List<VirtualFile> private_files = new ArrayList<>();
    private List<VirtualFile> public_files= new ArrayList<>();
    private List<VirtualFile> resource_files = new ArrayList<>();
    private List<VirtualFile> solution_files = new ArrayList<>();

    public Exercise(){
        this.id = UUID.randomUUID();
    }

    public void set(Exercise other){
        this.type = other.type;
        this.language = other.language;
    }

    public Optional<VirtualFile> getFileById(UUID id){
        Stream<VirtualFile> files = Stream.concat(Stream.concat(public_files.stream(), resource_files.stream()), solution_files.stream());
        return files.filter(file -> file.getId().equals(id)).findFirst();
    }
}
