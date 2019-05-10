package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.CourseRepository;
import ch.uzh.ifi.access.course.keycloak.KeycloakClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "course.users", value = "init-on-startup", havingValue = "true")
public class CourseServiceSetup {

    public CourseServiceSetup(KeycloakClient keycloakClient, CourseRepository courseRepository) {
        keycloakClient.enrollUsersInCourse(courseRepository.getCourse());
    }
}
