package ch.uzh.ifi.access.course.Model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileContent {
    private final UUID id;

    private String filePath;
    private String fileName;
    private String fileExtension;
    private String fileContent;
    private Boolean isMediaType;

    // Ignored by JSON
    private final List<String> MEDIA_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "mp3", "mp4");
    private File file;

    public FileContent(String fullPath){
        this.id = UUID.randomUUID();
        this.file = new File(fullPath);

        if(file.exists() && !file.isDirectory()){
            try {
                filePath = file.getCanonicalPath();
                String tmp[] = file.getName().split("\\.");
                if(tmp.length > 1) {
                    fileName = tmp[0];
                    fileExtension = tmp[1];

                    if(MEDIA_EXTENSIONS.contains(fileExtension)) {
                        fileContent = "";
                        isMediaType = true;
                    }else{
                        fileContent = new String(Files.readAllBytes(Paths.get(fullPath)));
                        isMediaType = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @JsonIgnore
    public List<String> getMEDIA_EXTENSIONS() {
        return MEDIA_EXTENSIONS;
    }

    @JsonIgnore
    public File getFile() {
        return file;
    }

    public UUID getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public Boolean getMediaType() {
        return isMediaType;
    }

    public void setMediaType(Boolean mediaType) {
        isMediaType = mediaType;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

