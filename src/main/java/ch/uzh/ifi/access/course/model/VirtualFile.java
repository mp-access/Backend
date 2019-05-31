package ch.uzh.ifi.access.course.model;


import ch.uzh.ifi.access.course.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Setter
public class VirtualFile {
    private static final List<String> MEDIA_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "mp3", "mp4");

    private String id;

    private String path;
    private String name;
    private String extension;
    private String content;
    private Boolean isMediaType;


    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private File file;

    public VirtualFile() {
        this.id = new Utils().getID();
    }

    public VirtualFile(String fullPath, String virtualPath) {
        this.id = new Utils().getID();
        this.file = new File(fullPath);
        this.path = virtualPath;

        if (file.exists() && !file.isDirectory()) {
            try {
                String[] tmp = file.getName().split("\\.");
                if (tmp.length > 1) {
                    name = tmp[0];
                    extension = tmp[1];

                    if (MEDIA_EXTENSIONS.contains(extension)) {
                        content = "";
                        isMediaType = true;
                    } else {
                        content = new String(Files.readAllBytes(Paths.get(fullPath)));
                        isMediaType = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        VirtualFile f = (VirtualFile) o;
        // field comparison
        return Objects.equals(this.path, f.path) &&
                Objects.equals(this.name, f.name) &&
                Objects.equals(this.extension, f.extension) &&
                Objects.equals(this.content, f.content) &&
                Objects.equals(this.isMediaType, f.isMediaType) &&
                Objects.equals(this.file, f.file);
    }

}

