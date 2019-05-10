package ch.uzh.ifi.access.course.Model.security;

import java.io.Serializable;

public class GrantedCourseAccess implements Serializable {

    private static final GrantedCourseAccess EMPTY = new GrantedCourseAccess("", false, false);

    private final String courseKey;

    private final boolean isStudent;

    private final boolean isAuthor;

    public GrantedCourseAccess(String courseKey, boolean isStudent, boolean isAuthor) {
        this.courseKey = courseKey;
        this.isStudent = isStudent;
        this.isAuthor = isAuthor;
    }

    public String getCourseKey() {
        return courseKey;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public static GrantedCourseAccess empty() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "GrantedCourseAccess{" +
                "courseKey='" + courseKey + '\'' +
                ", isStudent=" + isStudent +
                ", isAuthor=" + isAuthor +
                '}';
    }
}
