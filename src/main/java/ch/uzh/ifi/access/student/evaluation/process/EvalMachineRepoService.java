package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.config.statemachine.EvalMachineConfig;
import ch.uzh.ifi.access.student.dao.StateMachineMetaDataRepository;
import ch.uzh.ifi.access.student.model.evaluation.StateMachineMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EvalMachineRepoService {

    private StateMachineMetaDataRepository metaRepository;

    private StateMachineService<EvalMachine.States, EvalMachine.Events> machineService;

    private Map<String, StateMachine> machines;

    @Autowired
    public EvalMachineRepoService(StateMachineMetaDataRepository metaRepository, StateMachineService<EvalMachine.States, EvalMachine.Events> machineService) {
        this.metaRepository = metaRepository;
        this.machineService = machineService;
        this.machines = new HashMap<>();
    }

    public StateMachine initMachine(String submissionId, String user) {
        StateMachine m = machineService.acquireStateMachine(UUID.randomUUID().toString(), false);
        m.getExtendedState().getVariables().put(EvalMachineConfig.EXTENDED_VAR_SUBMISSION_ID, submissionId);
        m.getExtendedState().getVariables().put(EvalMachineConfig.EXTENDED_VAR_USER, user);
        metaRepository.save(StateMachineMetaData.builder().machineId(m.getId()).submissionId(submissionId).userId(user).build());
        machines.put(m.getId(), m);
        return m;
    }

    public StateMachine get(String machineId) {
        return machines.containsKey(machineId) ? machines.get(machineId) : machineService.acquireStateMachine(machineId,false);
    }

    public void store(String machineId, StateMachine machine) {
         machines.put(machineId, machine);
    }

}
