package ch.uzh.ifi.access.course.model.security;

import ch.uzh.ifi.access.course.model.Course;
import lombok.Value;

import java.io.Serializable;

@Value
public class GrantedCourseAccess implements Serializable {

    private static final GrantedCourseAccess EMPTY = new GrantedCourseAccess("", false, false);

    private final String course;

    private final boolean isStudent;

    private final boolean isAuthor;

    public GrantedCourseAccess(String courseKey, boolean isStudent, boolean isAuthor) {
        this.course = courseKey;
        this.isStudent = isStudent;
        this.isAuthor = isAuthor;
    }

    public boolean evaluateAccess(Course course) {
        return this.course.equals(course.getTitle());
    }

    public boolean evaluateAdminAccess(Course course) {
        return evaluateAccess(course) && isAuthor;
    }

    public static GrantedCourseAccess empty() {
        return EMPTY;
    }
}
