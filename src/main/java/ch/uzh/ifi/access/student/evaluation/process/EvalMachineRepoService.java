package ch.uzh.ifi.access.student.evaluation.process;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache of state machines used for the ACCESS MVP.
 * This should be replaced with a persistent version for later use.
 */
@Service
public class EvalMachineRepoService {

    private Map<String, StateMachine> machines;

    public EvalMachineRepoService() {
        this.machines = new ConcurrentHashMap<>();
    }

    public StateMachine get(String key) {
        return machines.get(key);
    }

    public void store(String key, StateMachine machine) {
        machines.put(key, machine);
    }

    public long count() {
        return machines.size();
    }

    public void removeMachinesOlderThan(Instant threshold) {
        machines.entrySet().removeIf(entry -> {
            StateMachine machine = entry.getValue();

            Map<Object, Object> machineVariables = machine.getExtendedState().getVariables();

            Instant startedTime = (Instant) machineVariables.get(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME);
            Instant completionTime = (Instant) machineVariables.get(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME);
            // A machine which has never completed and has been running for too long, should be removed
            boolean isZombieMachine = startedTime != null && startedTime.isBefore(threshold);

            // A machine which has completed normally and has finished for longer than threshold, can be safely removed
            boolean isSafeToRemove = completionTime != null && completionTime.isBefore(threshold);

            return isZombieMachine || isSafeToRemove;
        });
    }
}
