package ch.uzh.ifi.access.course.config;

import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.keycloak.KeycloakClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "access", value = "init-on-startup", havingValue = "true")
public class CourseServiceSetup {

    private KeycloakClient keycloakClient;

    private CourseDAO courseRepository;

    public CourseServiceSetup(KeycloakClient keycloakClient, CourseDAO courseRepository) {
        this.keycloakClient = keycloakClient;
        this.courseRepository = courseRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(backoff = @Backoff(delay = 30000))
    public void initializeAllCoursesParticipants() {
        List<Course> courses = courseRepository.selectAllCourses();
        courses.forEach(keycloakClient::enrollUsersInCourse);
    }

    @Retryable(backoff = @Backoff(delay = 30000))
    public void initializeCourseParticipants(Course course) {
        keycloakClient.enrollUsersInCourse(course);
    }
}
