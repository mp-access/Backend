package ch.uzh.ifi.access.course.model.security;

import lombok.Value;

import java.io.Serializable;

@Value
public class GrantedCourseAccess implements Serializable {

    private static final GrantedCourseAccess EMPTY = new GrantedCourseAccess("", false, false, false);

    private final String course;

    private final boolean isStudent;

    private final boolean isAssistant;

    private final boolean isAdmin;

    public GrantedCourseAccess(String courseKey, boolean isStudent, boolean isAssistant, boolean isAdmin) {
        this.course = courseKey;
        this.isStudent = isStudent;
        this.isAssistant = isAssistant;
        this.isAdmin = isAdmin;
    }

    public boolean evaluateAccess(String courseId) {
        return this.course.equals(courseId);
    }

    public boolean evaluateAssistantAccess(String courseId) {
        return evaluateAccess(courseId) && (isAssistant || isAdmin);
    }

    public boolean evaluateAdminAccess(String courseId) {
        return evaluateAccess(courseId) && isAdmin;
    }

    public static GrantedCourseAccess empty() {
        return EMPTY;
    }
}
