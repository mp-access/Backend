package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.util.RepoCacher;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository("gitrepo")
public class CourseDAO {

    private static final Logger logger = LoggerFactory.getLogger(CourseDAO.class);

    private static final String CONFIG_FILE = "repositories.json";

    private List<Course> courseList;

    private Map<String, Exercise> exerciseIndex;

    public CourseDAO() {
        ClassPathResource resource = new ClassPathResource(CONFIG_FILE);
        if (resource.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                URLList conf = mapper.readValue(resource.getFile(), URLList.class);
                courseList = RepoCacher.retrieveCourseData(conf.repositories);
                exerciseIndex = buildExerciseIndex(courseList);
                logger.info(String.format("Parsed %d courses", courseList.size()));

                writeParseResultsToFileSystem();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            courseList = null;
        }
    }

    private void writeParseResultsToFileSystem() {
        try {
            final String directoryPath = "courses_db";
            final String coursesFile = String.format("%s/courses.json", directoryPath);
            final String exerciseFile = String.format("%s/exercises.json", directoryPath);
            final Path directory = Paths.get(directoryPath).toAbsolutePath();
            Files.createDirectories(directory);

            FileOutputStream coursesJson = new FileOutputStream(coursesFile);
            FileOutputStream exercisesJson = new FileOutputStream(exerciseFile);

            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(coursesJson, courseList);
            writer.writeValue(exercisesJson, exerciseIndex);

            coursesJson.close();
            exercisesJson.close();
            logger.info(String.format("Written files: %s, %s", coursesFile, exerciseFile));
        } catch (IOException e) {
            logger.warn("Unable to write parse results to file system. Does the folder exists?");
        }
    }

    protected Map<String, Exercise> buildExerciseIndex(List<Course> courses) {
        return courses
                .stream()
                .flatMap(c -> c.getAssignments().stream())
                .flatMap(a -> a.getExercises().stream())
                .collect(Collectors.toUnmodifiableMap(Exercise::getId, ex -> ex));
    }

    public void updateCourses() {
        ClassPathResource resource = new ClassPathResource(CONFIG_FILE);
        if (resource.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                URLList conf = mapper.readValue(resource.getFile(), URLList.class);
                List<Course> courseUpdate = RepoCacher.retrieveCourseData(conf.repositories);

                for (int i = 0; i < courseList.size(); ++i) {
                    courseList.get(i).update(courseUpdate.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            courseList = null;
        }
    }

    public void updateCourseById(String id) {
        Course c = selectCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No course found"));
        try {
            List<Course> courseUpdate = RepoCacher.retrieveCourseData(new String[]{c.getGitURL()});
            c.update(courseUpdate.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Course> selectAllCourses() {
        return courseList;
    }

    public Optional<Course> selectCourseById(String id) {
        return courseList.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();
    }

    public Optional<Exercise> selectExerciseById(String id) {
        return Optional.ofNullable(exerciseIndex.get(id));
    }

    @Data
    private static class URLList {
        private String[] repositories;
    }
}
