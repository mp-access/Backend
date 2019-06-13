package ch.uzh.ifi.access.student.evaluation.process;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EvalMachineRepo {

    private Map<String, StateMachine> machines;

    public EvalMachineRepo() {
        this.machines = new HashMap<>();
    }

    public StateMachine get(String key) {
        return machines.get(key);
    }

    public void store(String key, StateMachine machine) {
         machines.put(key, machine);
    }

}
