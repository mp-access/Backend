package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.RepoCacher;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            courseList = null;
        }
    }

    protected Map<String, Exercise> buildExerciseIndex(List<Course> courses) {
        return courses
                .stream()
                .flatMap(c -> c.getAssignments().stream())
                .flatMap(a -> a.getExercises().stream())
                .collect(Collectors.toUnmodifiableMap(Exercise::getId, ex -> ex));
    }

    public void updateCourse() {
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
