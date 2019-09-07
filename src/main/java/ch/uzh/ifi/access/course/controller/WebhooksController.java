package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.ApiTokenAuthenticationProvider;
import ch.uzh.ifi.access.course.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhooksController {

    private final CourseService courseService;

    private static final Logger logger = LoggerFactory.getLogger(WebhooksController.class);

    public WebhooksController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping(path = "/courses/{id}/update/github")
    public void updateCourse(@PathVariable("id") String id, @RequestBody String json,
                             ApiTokenAuthenticationProvider.GithubHeaderAuthentication authentication) {
        logger.info("Received github web hook");

        if (!authentication.matchesHmacSignature(json)) {
            throw new BadCredentialsException("Hmac signature does not match!");
        }

        logger.info("Updating courses");
        courseService.updateCourseById(id);
    }

    @PostMapping(path = "/courses/{id}/update/gitlab")
    public void updateCourse(@PathVariable("id") String id,
                             ApiTokenAuthenticationProvider.GitlabHeaderAuthentication authentication) {
        logger.info("Received gitlab web hook");

        if (!authentication.isMatchesSecret()) {
            throw new BadCredentialsException("Header secret does not match!");
        }

        logger.info("Updating courses");
        courseService.updateCourseById(id);
    }
}
