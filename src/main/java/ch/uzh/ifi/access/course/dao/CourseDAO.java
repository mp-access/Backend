package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.RepoCacher;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository("gitrepo")
public class CourseDAO {

    private static final Logger logger = LoggerFactory.getLogger(CourseDAO.class);

    private static final String CONFIG_FILE = "repositories.json";

    private List<Course> courseList;

    public CourseDAO() {
        ClassPathResource resource = new ClassPathResource(CONFIG_FILE);
        if (resource.exists()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                URLList conf = mapper.readValue(resource.getFile(), URLList.class);
                courseList = RepoCacher.retrieveCourseData(conf.repositories);
                logger.info(String.format("Parsed %d courses", courseList.size()));

                // Shennanigens!
                List<Course> updateList = RepoCacher.retrieveCourseData(conf.repositories);
                updateList.get(0).getAssignments().remove(1);
                Assignment s = new Assignment();
                s.setTitle("Fake News");
                s.setDescription("LOLOLO");
                updateList.get(1).getAssignments().add(s);

                for(int i = 0; i < courseList.size(); ++i){
                    courseList.get(i).update(updateList.get(i));
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

    @Data
    private static class URLList {
        private String[] repositories;
    }
}
