package ch.uzh.ifi.access.student.evaluation.process;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class EvalMachineRepoServiceTest {

    @Test
    public void cleanUpNoMachines() {
        EvalMachineRepoService repo = new EvalMachineRepoService();
        repo.removeMachinesOlderThan(Instant.now());
    }

    @Test
    public void cleanUp() throws Exception {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        StateMachine<EvalMachine.States, EvalMachine.Events> m1 = EvalMachineFactory.initSMForSubmission("123");
        StateMachine<EvalMachine.States, EvalMachine.Events> m2 = EvalMachineFactory.initSMForSubmission("345");

        m1.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));
        m2.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(30, ChronoUnit.MINUTES));

        EvalMachineRepoService repo = new EvalMachineRepoService();
        repo.store(id1, m1);
        repo.store(id2, m2);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));

        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        repo.removeMachinesOlderThan(fiveMinutesAgo);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNull(repo.get(id2));
    }

    @Test
    public void noMachinesToClean() throws Exception {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        StateMachine<EvalMachine.States, EvalMachine.Events> m1 = EvalMachineFactory.initSMForSubmission("123");
        StateMachine<EvalMachine.States, EvalMachine.Events> m2 = EvalMachineFactory.initSMForSubmission("345");

        m1.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));
        m2.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));

        EvalMachineRepoService repo = new EvalMachineRepoService();
        repo.store(id1, m1);
        repo.store(id2, m2);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));

        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        repo.removeMachinesOlderThan(fiveMinutesAgo);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));
    }

    @Test
    public void oneMachineNotFinished() throws Exception {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        String id3 = UUID.randomUUID().toString();
        StateMachine<EvalMachine.States, EvalMachine.Events> m1 = EvalMachineFactory.initSMForSubmission("123");
        StateMachine<EvalMachine.States, EvalMachine.Events> m2 = EvalMachineFactory.initSMForSubmission("345");
        StateMachine<EvalMachine.States, EvalMachine.Events> m3 = EvalMachineFactory.initSMForSubmission("678");

        m1.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(10, ChronoUnit.MINUTES));
        m3.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));

        EvalMachineRepoService repo = new EvalMachineRepoService();
        repo.store(id1, m1);
        repo.store(id2, m2);
        repo.store(id3, m3);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));
        Assertions.assertNotNull(repo.get(id3));

        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        repo.removeMachinesOlderThan(fiveMinutesAgo);

        Assertions.assertNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));
        Assertions.assertNotNull(repo.get(id3));
    }

    @Test
    public void zombieMachine() throws Exception {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();
        String id3 = UUID.randomUUID().toString();

        StateMachine<EvalMachine.States, EvalMachine.Events> m1 = EvalMachineFactory.initSMForSubmission("123");
        StateMachine<EvalMachine.States, EvalMachine.Events> m2 = EvalMachineFactory.initSMForSubmission("345");
        StateMachine<EvalMachine.States, EvalMachine.Events> m3 = EvalMachineFactory.initSMForSubmission("678");

        m1.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME, Instant.now().minus(10, ChronoUnit.MINUTES));
        m1.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(10, ChronoUnit.MINUTES));
        m2.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME, Instant.now().minus(10, ChronoUnit.MINUTES));
        m3.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_STARTED_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));
        m3.getExtendedState().getVariables().put(EvalMachineFactory.EXTENDED_VAR_COMPLETION_TIME, Instant.now().minus(1, ChronoUnit.MINUTES));

        EvalMachineRepoService repo = new EvalMachineRepoService();
        repo.store(id1, m1);
        repo.store(id2, m2);
        repo.store(id3, m3);

        Assertions.assertNotNull(repo.get(id1));
        Assertions.assertNotNull(repo.get(id2));
        Assertions.assertNotNull(repo.get(id3));

        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        repo.removeMachinesOlderThan(fiveMinutesAgo);

        Assertions.assertNull(repo.get(id1));
        Assertions.assertNull(repo.get(id2));
        Assertions.assertNotNull(repo.get(id3));
    }
}