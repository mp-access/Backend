package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import org.springframework.stereotype.Service;

@Service
public class CoursePermissionEvaluator {

    /**
     * Evaluates if current logged in user has access to a course
     *
     * @param authentication contains the information to which course a user has access to
     * @param course         course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasAccessToCourse(CourseAuthentication authentication, Course course) {
        return authentication.hasAccess(course.getId());
    }

    /**
     * Evaluates if current logged in user has access to a course
     *
     * @param authentication contains the information to which course a user has access to
     * @param course         course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasAdminAccessToCourse(CourseAuthentication authentication, Course course) {
        return authentication.hasAdminAccess(course.getId());
    }

    /**
     * Evaluates if current logged in user has access to the exercise.
     * <p>
     * An admin always has access to an exercise. A normal user only if the assignment is past publishing date.
     *
     * @param authentication contains the information to which course a user has access to
     * @param exercise       exercise to access
     * @return true iff user has access to course, false otherwise
     */
    public boolean hasAccessToExercise(CourseAuthentication authentication, Exercise exercise) {
        Assignment assignment = exercise.getAssignment();
        return assignment.isPublished() || authentication.hasAdminAccess(exercise.getCourseId());
    }
}