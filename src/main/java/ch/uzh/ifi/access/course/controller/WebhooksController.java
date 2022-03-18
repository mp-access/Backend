package ch.uzh.ifi.access.course.controller;

import ch.uzh.ifi.access.config.AccessProperties;
import ch.uzh.ifi.access.course.service.CourseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("courses/update")
public class WebhooksController {

    private static final String GITHUB_HEADER_NAME = "X-Hub-Signature";
    private static final String GITLAB_HEADER_NAME = "X-Gitlab-Token";

    private AccessProperties accessProperties;

    private CourseService courseService;

    public WebhooksController(AccessProperties accessProperties, CourseService courseService) {
        this.accessProperties = accessProperties;
        this.courseService = courseService;
    }

    @PostMapping(path = "/{course}")
    public ResponseEntity<String> updateCourseById(@PathVariable("course") String course, @RequestHeader Map<String, String> headers,
                                                   @RequestBody JsonNode payload, Principal principal) {
        String githubHeader = headers.get(GITHUB_HEADER_NAME);
        if ((githubHeader != null) && githubHeader.startsWith("sha1=")) {
            log.info("Received a Github webhook for the course {}", course);
            if (accessProperties.getHmac() == null || accessProperties.getHmac().isEmpty())
                throw new UnsupportedOperationException("No hmac secret set! Exiting...");
            String hmacPayload = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, accessProperties.getHmac()).hmacHex(payload.toString());
            if (!principal.getName().equals("sha1=%s" + hmacPayload))
                throw new BadCredentialsException("Fail to process Github webhook - Hmac signature does not match!");
        } else {
            String gitlabHeader = headers.get(GITLAB_HEADER_NAME);
            if (gitlabHeader == null)
                return ResponseEntity.badRequest().build();
            else {
                log.info("Received a Gitlab webhook for the course {}", course);
                if (!gitlabHeader.equals(accessProperties.getGitlabWebhook()))
                    throw new BadCredentialsException("Fail to process Gitlab webhook - Header secret does not match!");
            }
        }

        log.info("Successfully validated webhook for course {}, initialising update...", course);
        courseService.updateCourseById(course);
        return ResponseEntity.accepted().build();
    }
}
