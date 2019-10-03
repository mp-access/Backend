package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.ApiTokenAuthenticationProvider;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.service.CourseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @PostMapping(path = "/courses/update/github")
    public ResponseEntity<?> updateCourseWithoutIdInUrl(@RequestBody JsonNode payload, ApiTokenAuthenticationProvider.GithubHeaderAuthentication authentication) {
        logger.info("Received github web hook");

        if (!authentication.matchesHmacSignature(payload.toString())) {
            throw new BadCredentialsException("Hmac signature does not match!");
        }

        return processWebhook(payload, false);
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

    @PostMapping(path = "/courses/update/gitlab")
    public ResponseEntity<?> updateCourseWithoutId(@RequestBody JsonNode payload, ApiTokenAuthenticationProvider.GitlabHeaderAuthentication authentication) {
        logger.info("Received gitlab web hook");

        if (!authentication.isMatchesSecret()) {
            throw new BadCredentialsException("Header secret does not match!");
        }

        return processWebhook(payload, true);
    }

    private ResponseEntity<String> processWebhook(JsonNode payload, boolean isGitlab) {
        logger.info("Updating course");
        WebhookPayload webhookPayload = new WebhookPayload(payload, isGitlab);
        Optional<Course> courseToUpdate = courseService.getAllCourses().stream().filter(course -> webhookPayload.matchesCourseUrl(course.getGitURL())).findFirst();
        courseToUpdate.ifPresent(c -> courseService.updateCourseById(c.getId()));
        return courseToUpdate.map(c -> ResponseEntity.accepted().body(c.getId())).orElse(ResponseEntity.notFound().build());
    }

    @Value
    public static class WebhookPayload {

        private JsonNode repository;

        private boolean isGitlab;

        public WebhookPayload(JsonNode root, boolean isGitlab) {
            this.repository = root.get("repository");
            this.isGitlab = isGitlab;
        }

        public String getHtmlUrl() {
            if (isGitlab) {
                return repository.get("homepage").asText();
            }
            return repository.get("html_url").asText();
        }

        public String getGitUrl() {
            if (isGitlab) {
                return repository.get("git_http_url").asText();
            }
            return repository.get("clone_url").asText();
        }

        public String getSshUrl() {
            if (isGitlab) {
                return repository.get("git_ssh_url").asText();
            }
            return repository.get("ssh_url").asText();
        }

        public boolean matchesCourseUrl(String courseUrl) {
            return courseUrl.equalsIgnoreCase(getHtmlUrl()) || courseUrl.equalsIgnoreCase(getGitUrl()) || courseUrl.equalsIgnoreCase(getSshUrl());
        }
    }
}
