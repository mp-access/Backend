package ch.uzh.ifi.access.course.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public class FileContent {

    // Ignored by JSON
    private static final List<String> MEDIA_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "mp3", "mp4");

    private final UUID id;

    private String filePath;
    private String fileName;
    private String fileExtension;
    private String fileContent;
    private Boolean isMediaType;

    @JsonIgnore
    private File file;

    public FileContent(String fullPath) {
        this.id = UUID.randomUUID();
        this.file = new File(fullPath);

        if (file.exists() && !file.isDirectory()) {
            try {
                filePath = file.getCanonicalPath();
                String tmp[] = file.getName().split("\\.");
                if (tmp.length > 1) {
                    fileName = tmp[0];
                    fileExtension = tmp[1];

                    if (MEDIA_EXTENSIONS.contains(fileExtension)) {
                        fileContent = "";
                        isMediaType = true;
                    } else {
                        fileContent = new String(Files.readAllBytes(Paths.get(fullPath)));
                        isMediaType = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

