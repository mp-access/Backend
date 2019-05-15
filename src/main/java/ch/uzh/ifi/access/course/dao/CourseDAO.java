package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.RepoCacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class URLList{
    public String repositories[];
}

@Repository("gitrepo")
public class CourseDAO {

    private final String CONFIG_FILE = "src/main/resources/repositories.json";

    private static List<Course> courseList;

    public CourseDAO(){
        ObjectMapper mapper = new ObjectMapper();
        if(new File(CONFIG_FILE).exists()){
            try {
                URLList conf = mapper.readValue(new File(CONFIG_FILE), URLList.class);
                courseList = RepoCacher.retrieveCourseData(conf.repositories);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            courseList = null;
        }
    }

    public List<Course> selectAllCourses() {
        return courseList;
    }

    public Optional<Course> selectCourseById(UUID id) {
        return courseList.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();
    }
}
