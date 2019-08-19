package ch.uzh.ifi.access.student.event;

import ch.uzh.ifi.access.course.event.BreakingChangeEvent;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BreakingChangeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BreakingChangeEvent.class);

    private final StudentSubmissionService submissionService;

    public BreakingChangeEventListener(StudentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @EventListener
    @Async
    public void handleBreakingChangeEvent(BreakingChangeEvent event) {
        logger.info(String.format("Received breaking change event %s: %s", event.getEventId(), event.getBreakingChangeExerciseIds()));

        submissionService.invalidateSubmissionsByExerciseIdIn(event.getBreakingChangeExerciseIds());
    }

}
