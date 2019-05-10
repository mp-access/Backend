package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Course;
import org.springframework.stereotype.Service;

@Service
public class CourseRepository {

    private final Course courseData;

    public CourseRepository() throws Exception {
        courseData = RepoCacher.retrieveCourseData();
    }

    public Course getCourse() {
        return courseData;
    }
}
