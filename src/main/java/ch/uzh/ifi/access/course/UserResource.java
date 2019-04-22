package ch.uzh.ifi.access.course;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserResource {

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
}