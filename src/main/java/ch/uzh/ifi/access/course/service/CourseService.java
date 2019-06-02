
package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseDAO courseDao;

    @Autowired
    public CourseService(@Qualifier("gitrepo") CourseDAO courseDao) {
        this.courseDao = courseDao;
    }

    public void updateCourses() {
        courseDao.updateCourse();
    }

    public List<Course> getAllCourses() {
        return courseDao.selectAllCourses();
    }

    public Optional<Course> getCourseById(String id) {
        return courseDao.selectCourseById(id);
    }

    public Optional<Exercise> getExerciseByCourseAndAssignmentId(String courseId, String assignmentId, String exerciseId) {
        return getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .flatMap(assignment -> assignment.findExerciseById(exerciseId));
    }

    public Optional<Exercise> getExerciseById(String exerciseId) {
        return courseDao.selectExerciseById(exerciseId);
    }

    public Optional<FileSystemResource> getFileByExerciseIdAndFileId(String exerciseId, String fileId) {
        Optional<Exercise> exercise = getExerciseById(exerciseId);
        Optional<VirtualFile> virtualFile = exercise.flatMap(e -> e.getFileById(fileId));

        return virtualFile.map(file -> new FileSystemResource(file.getFile()));
    }
}