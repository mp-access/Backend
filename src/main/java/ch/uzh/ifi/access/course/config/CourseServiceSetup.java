package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.course.keycloak.KeycloakClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ConditionalOnProperty(prefix = "course.users", value = "init-on-startup", havingValue = "true")
public class CourseServiceSetup {

    public CourseServiceSetup(KeycloakClient keycloakClient, CourseService courseRepository) {
        keycloakClient.enrollUsersInCourse(courseRepository.getCourseById(UUID.randomUUID()).orElse(null));
    }
}
