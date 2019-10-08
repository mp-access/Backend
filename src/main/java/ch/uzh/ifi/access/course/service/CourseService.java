
package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Assignment;
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

    private final CourseServiceSetup courseSetup;

    @Autowired
    public CourseService(@Qualifier("gitrepo") CourseDAO courseDao, CourseServiceSetup courseSetup) {
        this.courseDao = courseDao;
        this.courseSetup = courseSetup;
    }

    public void updateCourseById(String id) {
        Course course = courseDao.updateCourseById(id);
        if (course != null) {
            courseSetup.initializedCourseParticipants(course);
        }
    }

    public List<Course> getAllCourses() {
        return courseDao.selectAllCourses();
    }

    public Optional<Course> getCourseById(String id) {
        return courseDao.selectCourseById(id);
    }

    public Optional<List<Exercise>> getExercisesByCourseAndAssignmentId(String courseId, String assignmentId) {
        return getCourseById(courseId)
                .flatMap(course -> course.getAssignmentById(assignmentId))
                .map(Assignment::getExercises);
    }

    public Optional<Exercise> getExerciseById(String exerciseId) {
        return courseDao.selectExerciseById(exerciseId);
    }

    public Optional<FileSystemResource> getFileCheckingPrivileges(Exercise exercise, String fileId, CourseAuthentication authentication) {
        Optional<VirtualFile> virtualFile;
        if (hasAccessToExerciseSolutions(exercise, authentication)) {
            virtualFile = exercise.getAnyFileById(fileId);
        } else {
            virtualFile = exercise.getPublicOrResourcesFile(fileId);
        }
        return virtualFile.map(file -> new FileSystemResource(file.getFile()));
    }

    public Optional<Integer> getExerciseMaxSubmissions(String exerciseId) {
        return courseDao.selectExerciseById(exerciseId).map(Exercise::getMaxSubmits);
    }

    private boolean hasAccessToExerciseSolutions(Exercise exercise, CourseAuthentication authentication) {
        return exercise.isPastDueDate() || authentication.hasPrivilegedAccess(exercise.getCourseId());
    }
}