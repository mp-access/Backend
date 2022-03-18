package ch.uzh.ifi.access.course.event;

import ch.uzh.ifi.access.course.model.Exercise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BreakingChangeNotifier {

    private ApplicationEventPublisher eventPublisher;

    public BreakingChangeNotifier(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notifyBreakingChanges(List<Exercise> exercises) {
        if (exercises != null && !exercises.isEmpty()) {
            List<String> ids = exercises.stream().map(Exercise::getId).collect(Collectors.toList());
            BreakingChangeEvent event = new BreakingChangeEvent(this, ids);
            log.info("Publishing breaking change event {}: {}", event.getEventId(), event.getBreakingChangeExerciseIds());
            eventPublisher.publishEvent(event);
        }
    }
}
