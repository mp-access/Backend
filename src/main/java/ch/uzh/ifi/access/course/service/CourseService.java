package ch.uzh.ifi.access.course.service;

import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Async("courseUpdateWorkerExecutor")
    public void updateCourseById(String id) {
        Course updatedCourse = courseDao.updateCourseById(id);
        if (updatedCourse != null) {
            courseSetup.initializeCourseParticipants(updatedCourse);
        }
    }

    public List<Course> getAllCourses() {
        return courseDao.selectAllCourses();
    }

    public Optional<Course> getCourseByRoleName(String roleName) {
        return courseDao.selectCourseByRoleName(roleName);
    }

    public Optional<Exercise> getExerciseById(String exerciseId) {
        return courseDao.selectExerciseById(exerciseId);
    }

    /**
     * Check whether the user's role name list includes the requested course role name, and if yes search for the
     * course by its role name in the list of cached courses.
     * @param courseId  requested course role name
     * @return          Course object matching the requested role name, if accessible and found
     * @throws AccessDeniedException      if the user does not have the requested course role name
     * @throws ResourceNotFoundException  if the requested course is accessible but not in the cached courses list
     */
    @PreAuthorize("hasRole(#courseId)")
    public Course getCourseWithPermission(String courseId) {
        return getCourseByRoleName(courseId).orElseThrow(() -> new ResourceNotFoundException("No course found"));
    }

    /**
     * Get an exercise by its ID and return it only if the user who made the request can access the exercise.
     *
     * Permission to view the exercise is granted when the following conditions are satisfied:
     * (1) The user has access to the exercise's course
     * (2) The exercise has been published already, or the user has an assistant role for the exercise's course
     *
     * Permission to submit a solution to the exercise is granted when the following conditions are satisfied:
     * (1) The user has access to the exercise's course
     * (2) The exercise has been published already and submission deadline has not yet passed, or
     *      the user has an assistant role for the exercise's course
     * @param exerciseId    requested exercise ID
     * @param isGraded      if true, the permission to submit is evaluated; if false, the permission to view is evaluated
     * @return              Exercise object matching the requested ID, if accessible for viewing and found
     * @throws AccessDeniedException      if the user does not meet all authorization conditions for the permission type
     * @throws ResourceNotFoundException  if the exercise was not found
     */
    @PostAuthorize("hasRole(returnObject.courseId) and " +
        "((returnObject.published and not (#isGraded and returnObject.pastDueDate)) or hasRole(returnObject.courseId + '-assistant'))")
    public Exercise getExerciseWithPermission(String exerciseId, boolean isGraded) {
        return getExerciseById(exerciseId).orElseThrow(() -> new ResourceNotFoundException("No exercise found for ID"));
    }

    /**
     * @throws AccessDeniedException  if the user does not meet all authorization conditions for viewing permission
     * @see #getExerciseWithPermission(String, boolean)
     */
    @PostAuthorize("hasRole(returnObject.courseId) and (returnObject.published or hasRole(returnObject.courseId + '-assistant'))")
    public Exercise getExerciseWithViewPermission(String exerciseId) {
        return getExerciseWithPermission(exerciseId, false);
    }
}