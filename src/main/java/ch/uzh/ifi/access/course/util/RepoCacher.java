package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.course.model.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RepoCacher {

    // FOLDERS
    private static final String ASSIGNMENT_FOLDER_PREFIX = "assignment_";
    private static final String EXERCISE_FOLDER_PREFIX = "exercise_";
    private static final String PUBLIC_FOLDER_NAME = "public";
    private static final String PRIVATE_FOLDER_NAME = "private";
    private static final String RESOURCE_FOLDER_NAME = "resource";
    private static final String SOLUTION_FOLDER_NAME = "solution";

    // FILES
    private static final String COURSE_FILE_NAME = "config.json";
    private static final String ASSIGNMENT_FILE_NAME = "config.json";
    private static final String EXERCISE_FILE_NAME = "config.json";
    private static final String QUESTION_FILE_NAME = "description.md";

    private GitClient gitClient;

    private ObjectMapper mapper;

    public RepoCacher(GitClient gitClient) {
        this.gitClient = gitClient;
        this.mapper = initMapper();
    }

    private List<String> ignoreDir = List.of(".git");
    private List<String> ignoreFile = List.of(".gitattributes", ".gitignore", "README.md");

    public List<Course> retrieveCourseData(List<String> urls) {
        List<Course> courses = new ArrayList<>();
        for (String url : urls) {
            try {
                String hash = gitClient.pullOrClone(url);

                File repo = gitClient.getRepoDir(url);
                Course course = new Course(repo.getName());
                course.setGitHash(hash);
                course.setDirectory(repo.getAbsolutePath());
                course.setGitURL(url);

                cacheRepo(repo, course);
                courses.add(course);
            } catch (Exception e) {
                log.error("Failed to pull repository: {}", url, e);
            }
        }
        return courses;
    }

    public List<Course> retrieveLocalCourseData(List<File> repos) {
        List<Course> courses = new ArrayList<>();
        for (File repo : repos) {
            try {
                Course course = new Course(repo.getName());
                course.setGitHash("ThisIsARealHash");
                course.setDirectory(repo.getAbsolutePath());

                cacheRepo(repo, course);
                courses.add(course);
            } catch (Exception e) {
                log.error(String.format("Failed to cache repository: %s", repo.getName()), e);
            }
        }
        return courses;
    }

    private String readFile(File file) {
        try {
            String content = Files.readString(file.toPath());
            log.trace("Parsed file {}\nContent:\n{}", file.getAbsolutePath(), content);
            return content;
        } catch (Exception e) {
            log.warn("Failed to parse file {}", file.getAbsolutePath(), e);
            return "";
        }
    }

    private void cacheRepo(File file, Object context) {
        if (file.isDirectory()) {
            if (ignoreDir.contains(file.getName())) return;

            Object next_context = context;
            if (file.getName().startsWith(ASSIGNMENT_FOLDER_PREFIX)) {
                Course c = ((Course) context);

                String cleanName = cleanFolderName(file.getName());
                int order = Integer.parseInt(cleanName.replace(ASSIGNMENT_FOLDER_PREFIX, ""));

                Assignment assignment = new Assignment(c.getGitURL() + cleanName);
                assignment.setOrder(order);
                c.addAssignment(assignment);
                next_context = assignment;
            } else if (file.getName().startsWith(EXERCISE_FOLDER_PREFIX)) {
                Assignment a = ((Assignment) context);

                String cleanName = cleanFolderName(file.getName());
                int order = Integer.parseInt(cleanName.replace(EXERCISE_FOLDER_PREFIX, ""));

                Exercise exercise = new Exercise(a.getId() + cleanName);
                exercise.setOrder(order);
                exercise.setGitHash(((Assignment) context).getCourse().getGitHash());
                a.addExercise(exercise);
                next_context = exercise;
            } else if (file.getName().startsWith(PUBLIC_FOLDER_NAME)) {
                // TODO investigate if it would make more sense to leave the root folder in tha path
                listFiles(file, ((Exercise) context).getPublic_files(), file.getPath());
            } else if (file.getName().startsWith(PRIVATE_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getPrivate_files(), file.getPath());
            } else if (file.getName().startsWith(RESOURCE_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getResource_files(), file.getPath());
            } else if (file.getName().startsWith(SOLUTION_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getSolution_files(), file.getPath());
            }

            String[] children = file.list();
            for (String child : children) {
                cacheRepo(new File(file, child), next_context);
            }
        } else {
            if (ignoreFile.contains(file.getName())) return;

            if (context instanceof Course) {
                if (file.getName().equals(COURSE_FILE_NAME)) {
                    try {
                        ((Course) context).set(mapper.readValue(file, CourseConfig.class));
                    } catch (Exception e) {
                        log.error("Error while parsing Course information: {}", file.getName());
                        e.printStackTrace();
                    }
                }
            } else if (context instanceof Assignment) {
                if (file.getName().equals(ASSIGNMENT_FILE_NAME)) {
                    try {
                        ((Assignment) context).set(mapper.readValue(file, AssignmentConfig.class));
                    } catch (Exception e) {
                        log.error("Error while parsing Assignment information: {}", file.getName());
                        e.printStackTrace();
                    }
                }
            } else if (context instanceof Exercise) {
                if (file.getName().equals(QUESTION_FILE_NAME)) {
                    ((Exercise) context).setQuestion(readFile(file));
                } else if (file.getName().equals(EXERCISE_FILE_NAME)) {
                    try {
                        ((Exercise) context).set(mapper.readValue(file, ExerciseConfig.class));
                    } catch (Exception e) {
                        log.error("Error while parsing Exercise information: {}", file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void listFiles(File dir, List<VirtualFile> fileList, String root) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                listFiles(new File(dir, child), fileList, root);
            }
        } else {
            fileList.add(new VirtualFile(dir.getAbsolutePath(), dir.getPath().replace(root, "")));
        }
    }

    private String nameFromGitURL(String url) {
        return url
                .replace("https://github.com/", "")
                .replace("git@gitlab.com:", "")
                .replace("https://gitlab.com:", "")
                .replace(".git", "")
                .replace(":", "_");
    }

    private String cleanFolderName(String name) {
        String cleanName = name;
        int commentIndex = StringUtils.ordinalIndexOf(name, "_", 2);

        if (commentIndex != -1)
            cleanName = name.substring(0, commentIndex);

        return cleanName;
    }

    private ObjectMapper initMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule().addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer()))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
    }
}