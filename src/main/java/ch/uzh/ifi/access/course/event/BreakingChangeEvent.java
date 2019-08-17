package ch.uzh.ifi.access.course.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
public class BreakingChangeEvent extends ApplicationEvent {

    private UUID eventId;

    private List<String> breakingChangeExerciseIds;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source                    the object on which the event initially occurred (never {@code null})
     * @param breakingChangeExerciseIds ids of the exercises with breaking changes
     */
    public BreakingChangeEvent(Object source, List<String> breakingChangeExerciseIds) {
        super(source);
        this.breakingChangeExerciseIds = breakingChangeExerciseIds;
        eventId = UUID.randomUUID();
    }
}
