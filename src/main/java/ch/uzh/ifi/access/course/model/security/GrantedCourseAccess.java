package ch.uzh.ifi.access.course.model.security;

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

    public boolean evaluateAccess(String courseId) {
        return this.course.equals(courseId);
    }

    public boolean evaluateAdminAccess(String courseId) {
        return evaluateAccess(courseId) && isAuthor;
    }

    public static GrantedCourseAccess empty() {
        return EMPTY;
    }
}
