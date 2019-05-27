package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "course.users", value = "init-on-startup", havingValue = "true")
public class CourseServiceSetup {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceSetup.class);

    private KeycloakClient keycloakClient;

    private CourseDAO courseRepository;

    public CourseServiceSetup(KeycloakClient keycloakClient, CourseDAO courseRepository) {
        this.keycloakClient = keycloakClient;
        this.courseRepository = courseRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(
            backoff = @Backoff(delay = 5000)
    )
    public void initializedCourseParticipants() {
        List<Course> courses = courseRepository.selectAllCourses();
        courses.forEach(keycloakClient::enrollUsersInCourse);
    }

    @SuppressWarnings("unused")
    @Recover
    void logFailedAttemptToInitializeParticipants() {
        logger.warn("Failed to initialize participants: could not connect to identity provider");
    }
}
