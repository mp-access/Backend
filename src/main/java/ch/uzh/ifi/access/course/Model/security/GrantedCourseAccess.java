package ch.uzh.ifi.access.course.Model.security;

import ch.uzh.ifi.access.course.Model.Course;

import java.io.Serializable;

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

    public String getCourse() {
        return course;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public boolean evaluateAccess(Course course) {
        return this.course.equals(course.title);
    }

    public static GrantedCourseAccess empty() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "GrantedCourseAccess{" +
                "course='" + course + '\'' +
                ", isStudent=" + isStudent +
                ", isAuthor=" + isAuthor +
                '}';
    }
}
