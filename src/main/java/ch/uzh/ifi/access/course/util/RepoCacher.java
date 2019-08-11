package ch.uzh.ifi.access.course.util;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepoCacher {

    private static final Logger logger = LoggerFactory.getLogger(RepoCacher.class);

    private static final String REPO_DIR = "course_repositories";

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

    private static ObjectMapper mapper;

    private List<String> ignore_dir = Arrays.asList(".git");
    private List<String> ignore_file = Arrays.asList(".gitattributes", ".gitignore", "README.md");

    public static List<Course> retrieveCourseData(String urls[]) throws Exception {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(fmt);
        javaTimeModule.addDeserializer(LocalDateTime.class, deserializer);

        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.registerModule(javaTimeModule);

        List<Course> courses = new ArrayList<>();

        int i = 0;
        for (String url : urls) {
            String hash = loadFilesFromGit(url);

            RepoCacher cacher = new RepoCacher();
            File repo = new File(REPO_DIR + "/" + nameFromGitURL(url));
            Course course = new Course();
            course.setGitHash(hash);
            course.setDirectory(repo.getAbsolutePath());
            course.setGitURL(url);

            cacher.cacheRepo(repo, course);
            courses.add(course);
        }
        return courses;
    }

    private static String readFile(File file) {
        try {
            byte[] data = null;
            FileInputStream fis = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String content = new String(data, StandardCharsets.UTF_8);
            logger.trace(String.format("Parsed file %s\nContent:\n%s", file.getAbsolutePath(), content));
            return content;
        } catch (Exception e) {
            logger.warn(String.format("Failed to parse file %s", file.getAbsolutePath()), e);
            return "";
        }
    }

    private void cacheRepo(File file, Object context) {
        if (file.isDirectory()) {
            if (ignore_dir.contains(file.getName())) return;

            Object next_context = context;
            if (file.getName().startsWith(ASSIGNMENT_FOLDER_PREFIX)) {
                Assignment assignment = new Assignment();
                assignment.setIndex(Integer.parseInt(file.getName().replace(ASSIGNMENT_FOLDER_PREFIX, "")));
                ((Course) context).addAssignment(assignment);
                next_context = assignment;
            } else if (file.getName().startsWith(EXERCISE_FOLDER_PREFIX)) {
                Exercise exercise = new Exercise();
                exercise.setIndex(Integer.parseInt(file.getName().replace(EXERCISE_FOLDER_PREFIX, "")));
                exercise.setGitHash(((Assignment) context).getCourse().getGitHash());
                ((Assignment) context).addExercise(exercise);
                next_context = exercise;
            } else if (file.getName().startsWith(PUBLIC_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getPublic_files(), file.getPath());
            } else if (file.getName().startsWith(PRIVATE_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getPrivate_files(), file.getPath());
            } else if (file.getName().startsWith(RESOURCE_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getResource_files(), file.getPath());
            } else if (file.getName().startsWith(SOLUTION_FOLDER_NAME)) {
                listFiles(file, ((Exercise) context).getSolution_files(), file.getPath());
            }

            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
                cacheRepo(new File(file, children[i]), next_context);
        } else {
            if (ignore_file.contains(file.getName())) return;

            if (context instanceof Course) {
                if (file.getName().equals(COURSE_FILE_NAME)) {
                    try {
                        ((Course) context).set(mapper.readValue(file, Course.class));
                    } catch (Exception e) {
                        System.err.println("Error while parsing Course information: " + file.getName());
                        e.printStackTrace();
                    }
                }
            } else if (context instanceof Assignment) {
                if (file.getName().equals(ASSIGNMENT_FILE_NAME)) {
                    try {
                        ((Assignment) context).set(mapper.readValue(file, Assignment.class));
                    } catch (Exception e) {
                        System.err.println("Error while parsing Assignment information: " + file.getName());
                        e.printStackTrace();
                    }
                }
            } else if (context instanceof Exercise) {
                if (file.getName().equals(QUESTION_FILE_NAME)) {
                    ((Exercise) context).setQuestion(readFile(file));
                } else if (file.getName().equals(EXERCISE_FILE_NAME)) {
                    try {
                        ((Exercise) context).set(mapper.readValue(file, Exercise.class));
                    } catch (Exception e) {
                        System.err.println("Error while parsing Exercise information: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void listFiles(File dir, List<VirtualFile> fileList, String root) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
                listFiles(new File(dir, children[i]), fileList, root);
        } else {
            fileList.add(new VirtualFile(dir.getAbsolutePath(), dir.getPath().replace(root, "")));
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
                deleteDir(new File(dir, children[i]));
        }
        return dir.delete();
    }

    private static String nameFromGitURL(String url) {
        return url.replace("https://github.com/", "").replace(".git", "");
    }

    private static String loadFilesFromGit(String url) throws Exception {
        File gitDir = new File(REPO_DIR + "/" + nameFromGitURL(url));
        if (gitDir.exists()) {
            new Git(new FileRepository(new File(REPO_DIR + "/" + nameFromGitURL(url) + "/.git")))
                    .pull()
                    .call();
        } else {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(gitDir)
                    .call();
        }

        return (new FileRepository(new File(REPO_DIR + "/" + nameFromGitURL(url) + "/.git")).getAllRefs().get("HEAD").getObjectId().getName());
    }
}