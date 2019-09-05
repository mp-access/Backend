package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.security.GrantedCourseAccess;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoursePermissionEvaluatorTest {

    private CoursePermissionEvaluator evaluator = new CoursePermissionEvaluator();

    @Test
    public void hasAccessToCourseStudent() {
        String courseName = "Info1";
        String someOtherCourseName = "Info2";
        Course course = new Course("");
        course.setTitle(courseName);
        GrantedCourseAccess info1 = new GrantedCourseAccess(course.getId(), true, false);
        GrantedCourseAccess info2 = new GrantedCourseAccess(someOtherCourseName, true, false);

        CourseAuthentication courseAuthentication = authentication(Set.of(info1, info2));

        Assert.assertTrue(evaluator.hasAccessToCourse(courseAuthentication, course));
    }

    @Test
    public void hasNoAccessToCourse() {
        String courseName = "Info1";
        String someOtherCourseName = "Info2";
        GrantedCourseAccess info1 = new GrantedCourseAccess(someOtherCourseName, true, false);
        Course course = new Course("");
        course.setTitle(courseName);

        CourseAuthentication courseAuthentication = authentication(Set.of(info1));

        Assert.assertFalse(evaluator.hasAccessToCourse(courseAuthentication, course));
    }

    private static CourseAuthentication authentication(Set<GrantedCourseAccess> grantedCourseAccesses) {
        OAuth2Request request = new OAuth2Request(Map.of(),
                "client",
                List.of(), true,
                Set.of("openid"),
                Set.of(), null, null, null);
        return new CourseAuthentication(request, null, grantedCourseAccesses, "");
    }
}