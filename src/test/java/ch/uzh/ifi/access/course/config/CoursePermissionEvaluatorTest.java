package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CoursePermissionEvaluatorTest {

    private CoursePermissionEvaluator evaluator = new CoursePermissionEvaluator();

    @Test
    public void hasAccessToCourseStudent() {
        String courseName = "Info1";
        String someOtherCourseName = "Info2";
        Course course = new Course("");
        course.setTitle(courseName);
        GrantedCourseAccess info1 = new GrantedCourseAccess(course.getId(), true, false, false);
        GrantedCourseAccess info2 = new GrantedCourseAccess(someOtherCourseName, true, false, false);

        CourseAuthentication courseAuthentication = TestObjectFactory.createCourseAuthentication(Set.of(info1, info2));

        Assertions.assertTrue(evaluator.hasAccessToCourse(courseAuthentication, course));
    }

    @Test
    public void hasNoAccessToCourse() {
        String courseName = "Info1";
        String someOtherCourseName = "Info2";
        GrantedCourseAccess info1 = new GrantedCourseAccess(someOtherCourseName, true, false, false);
        Course course = new Course("");
        course.setTitle(courseName);

        CourseAuthentication courseAuthentication = TestObjectFactory.createCourseAuthentication(Set.of(info1));

        Assertions.assertFalse(evaluator.hasAccessToCourse(courseAuthentication, course));
    }
}