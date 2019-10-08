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
        for (String machineKey : machines.keySet()) {
            StateMachine machine = machines.get(machineKey);
            Instant completionTime = (Instant) machine.getExtendedState().getVariables().get(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME);
            if (completionTime.isBefore(threshold)) {
                machines.remove(machineKey);
            }
        }
    }
}
