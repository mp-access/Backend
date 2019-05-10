package ch.uzh.ifi.access.course.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('student')")
    public ResponseEntity<Principal> getUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping(path = "/admin")
    @PreAuthorize("hasAnyAuthority('author')")
    public ResponseEntity<String> getAdmin(Principal principal) {
        return ResponseEntity.ok("ggwp admin " + principal.getName());
    }

    @GetMapping("/demo")
    public ResponseEntity<List<String>> demo() {
        return ResponseEntity.ok(List.of("Alice", "Bob", "Ollie"));
    }

    @GetMapping("/courses")
    public Object getCourses(OAuth2Authentication principal) {
        Map<String, Serializable> extensions = principal.getOAuth2Request().getExtensions();
        Serializable courses = extensions.get("courses");

        logger.info("Fetched courses: " + courses.toString());
        return courses;
    }
}