package ch.uzh.ifi.access.course.api;

import ch.uzh.ifi.access.course.Model.security.GrantedCourseAccess;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GetMapping
    public ResponseEntity<Principal> getUser(Principal principal) {
        return ResponseEntity.ok(principal);
    }

    @GetMapping(path = "/admin")
    public ResponseEntity<String> getAdmin(Principal principal) {
        return ResponseEntity.ok("ggwp admin " + principal.getName());
    }

    @GetMapping("/courses")
    public List<String> getCourses(CourseAuthentication authentication) {
        return authentication.getCourseAccesses().stream().map(GrantedCourseAccess::getCourse).collect(Collectors.toList());
    }
}