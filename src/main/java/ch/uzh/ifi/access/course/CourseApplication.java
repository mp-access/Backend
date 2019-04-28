package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.keycloak.KeycloakClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
public class CourseApplication {

    private static final Logger logger = LoggerFactory.getLogger(CourseApplication.class);

    public static Course courseData;

    private final KeycloakClient keycloakClient;

    public CourseApplication(KeycloakClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

    @Bean
    CommandLineRunner runner() {
        return (args) -> {
            try {
                keycloakClient.addCourseClaims();
                courseData = RepoCacher.retrieveCourseData();
            } catch (Exception e) {
                // Do nothing for now
                logger.error("Failed to initialize stuff", e);
            }
        };
    }

}
