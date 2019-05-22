package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "course.users", value = "init-on-startup", havingValue = "true")
public class CourseServiceSetup {

    public CourseServiceSetup(KeycloakClient keycloakClient, CourseDAO courseRepository) {
        List<Course> courses = courseRepository.selectAllCourses();
        courses.forEach(keycloakClient::enrollUsersInCourse);
    }
}
