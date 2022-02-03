//package ch.uzh.ifi.access.keycloak;
//
//import ch.uzh.ifi.access.config.SecurityProperties;
//import lombok.extern.slf4j.Slf4j;
//import org.keycloak.admin.client.resource.UserResource;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Recover;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Retryable email sender.
// * <p>
// * Emails aren't actually sent from the course-service but from the keycloak API.
// * <p>
// * This component wraps the email sending logic in order to retry in case of failure.
// * The number of attempts and delay between attempts can be configured via applicaiton.properties.
// */
//@Slf4j
//@Component
//public class EmailSender {
//
//    private static final List<String> emailActionsAfterCreation = List.of("VERIFY_EMAIL", "UPDATE_PASSWORD", "UPDATE_PROFILE");
//    private SecurityProperties securityProperties;
//
//    public EmailSender(SecurityProperties securityProperties) {
//        this.securityProperties = securityProperties;
//    }
//
//    @Retryable(
//            maxAttemptsExpression = "${course.users.email.max-attempts}",
//            backoff = @Backoff(delayExpression = "${course.users.email.delay}")
//    )
//    public void sendEmailToUser(UserResource user) {
//        var frontendClientId = securityProperties.getFrontendClientId();
//        var redirectUri = securityProperties.getRedirectUriAfterActions();
//
//        user.executeActionsEmail(frontendClientId, redirectUri, emailActionsAfterCreation);
//    }
//
//    @Recover
//    @SuppressWarnings("unused")
//    private void logFailedToSendEmail(Exception e, UserResource user) {
//        UserRepresentation userRepresentation = user.toRepresentation();
//
//        throw new FailedToSendEmailException("Failed to send email to user '{}'", e);
//    }
//
//    public static class FailedToSendEmailException extends RuntimeException {
//
//        public FailedToSendEmailException(String message, Throwable cause) {
//            super(message, cause);
//        }
//    }
//}
