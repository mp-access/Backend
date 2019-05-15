package ch.uzh.ifi.access.course.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileContent {
    private String filePath;
    private String fileName;
    private String fileExtension;
    private String fileContent;

    public FileContent(String fullPath){
        File f = new File(fullPath);
        if(f.exists() && !f.isDirectory()){
            try {
                filePath = f.getCanonicalPath();
                String tmp[] = f.getName().split("\\.");
                if(tmp.length > 1) {
                    fileName = tmp[0];
                    fileExtension = tmp[1];
                }
                fileContent = new String(Files.readAllBytes(Paths.get(fullPath)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFullPath() {
        return filePath;
    }

    public String getContent() {
        return fileContent;
    }
}
