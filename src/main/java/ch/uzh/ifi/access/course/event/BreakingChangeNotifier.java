package ch.uzh.ifi.access.course.event;

import ch.uzh.ifi.access.course.model.Exercise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreakingChangeNotifier {

    private static final Logger logger = LoggerFactory.getLogger(BreakingChangeNotifier.class);

    private final ApplicationEventPublisher eventPublisher;

    public BreakingChangeNotifier(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notifyBreakingChanges(List<Exercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            List<String> ids = exercises.stream().map(Exercise::getId).collect(Collectors.toList());
            BreakingChangeEvent event = new BreakingChangeEvent(this, ids);
            logger.info(String.format("Publishing breaking change event %s: %s)", event.getEventId(), event.getBreakingChangeExerciseIds()));
            eventPublisher.publishEvent(event);
        }
    }
}
