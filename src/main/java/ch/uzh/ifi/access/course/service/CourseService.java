
package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseDAO courseDao;

    @Autowired
    public CourseService(@Qualifier("gitrepo") CourseDAO courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> getAllCourses() {
        return courseDao.selectAllCourses();
    }

    public Optional<Course> getCourseById(UUID id) {
        return courseDao.selectCourseById(id);
    }

    public List<Assignment> getAllAssignmentsByCourseID() { return null;}

    public Optional<Course> getAssignmentByIdByCourseId(UUID id) {
        return courseDao.selectCourseById(id);
    }
}