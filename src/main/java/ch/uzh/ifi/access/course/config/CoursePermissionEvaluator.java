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
     * @param courseId       course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasAccessToCourse(CourseAuthentication authentication, String courseId) {
        return authentication.hasAccess(courseId);
    }

    /**
     * Evaluates if current logged in user has access to a course
     *
     * @param authentication contains the information to which course a user has access to
     * @param course         course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasPrivilegedAccessToCourse(CourseAuthentication authentication, Course course) {
        return authentication.hasPrivilegedAccess(course.getId());
    }

    /**
     * Evaluates if current logged in user has access to a course
     *
     * @param authentication contains the information to which course a user has access to
     * @param courseId         course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasPrivilegedAccessToCourse(CourseAuthentication authentication, String courseId) {
        return authentication.hasPrivilegedAccess(courseId);
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
     * Evaluates if current logged in user has access to a course
     *
     * @param authentication contains the information to which course a user has access to
     * @param courseId         course to access
     * @return
     * @see CourseAuthentication#getCourseAccesses()
     */
    public boolean hasAdminAccessToCourse(CourseAuthentication authentication, String courseId) {
        return authentication.hasAdminAccess(courseId);
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
        String courseId = exercise.getCourseId();
        return authentication.hasAccess(courseId) && (assignment.isPublished() || authentication.hasPrivilegedAccess(courseId));
    }
}