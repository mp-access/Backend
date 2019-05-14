package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CourseRepository {

    private final Course courseData[];
    private final String CONFIG_FILE = "src/main/resources/repositories.json";

    public CourseRepository() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if(new File(CONFIG_FILE).exists()){
            UrlConf conf = mapper.readValue(new File(CONFIG_FILE), UrlConf.class);
            courseData = RepoCacher.retrieveCourseData(conf.repositories);
            System.out.println(courseData);
        }else{
            courseData = null;
        }
    }

    public Course getCourse(int id) {
        return courseData[id];
    }
}

class UrlConf{
    public String repositories[];
}