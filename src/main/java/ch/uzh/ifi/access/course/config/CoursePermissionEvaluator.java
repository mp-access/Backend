package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.model.Course;
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
        return authentication.hasAccess(course);
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
        return authentication.hasAdminAccess(course);
    }

}