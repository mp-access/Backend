package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.Events;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache of state machines used for the ACCESS MVP.
 * This should be replaced with a persistent version for later use.
 */
@Service
public class EvalMachineRepoService {

    private static final Logger logger = LoggerFactory.getLogger(EvalMachineRepoService.class);

    private Map<String, StateMachine<States, Events>> machines;

    public EvalMachineRepoService() {
        this.machines = new ConcurrentHashMap<>();
    }

    public StateMachine<States, Events> get(String key) {
        return machines.get(key);
    }

    public void store(String key, StateMachine<States, Events> machine) {
        machines.put(key, machine);
    }

    public long count() {
        return machines.size();
    }

    public void removeMachinesOlderThan(Instant threshold) {
        machines.entrySet().removeIf(entry -> {
            StateMachine<States, Events> machine = entry.getValue();

            Map<Object, Object> machineVariables = machine.getExtendedState().getVariables();

            Instant startedTime = (Instant) machineVariables.get(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME);
            Instant completionTime = (Instant) machineVariables.get(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME);
            // A machine which has never completed and has been running for too long, should be removed
            boolean isZombieMachine = startedTime != null && startedTime.isBefore(threshold) && completionTime == null;
            if (isZombieMachine) {
                logger.info("Found a zombie machine {}, will forcibly set state to {}. Started {}: {}", entry.getKey(), EvalMachine.Events.FINISH, startedTime, machine.toString());
                try {
                    machine.sendEvent(Mono.just(MessageBuilder.withPayload(EvalMachine.Events.FINISH).build())).subscribe();
                } catch (Exception e) {
                    logger.error("Failed to forcibly set the state of the zombie machine to {}", EvalMachine.Events.FINISH, e);
                }
            }

            // A machine which has completed normally and has finished for longer than threshold, can be safely removed
            boolean isSafeToRemove = completionTime != null && completionTime.isBefore(threshold);

            return isZombieMachine || isSafeToRemove;
        });
    }
}
