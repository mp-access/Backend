package ch.uzh.ifi.access.config.statemachine;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

@Configuration
public class EvalMachineServiceConfig {

    @Bean
    public StateMachineRuntimePersister<EvalMachine.States, EvalMachine.Events, String> stateMachineRuntimePersister(
            MongoDbStateMachineRepository jpaStateMachineRepository) {
        return new MongoDbPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<EvalMachine.States, EvalMachine.Events> stateMachineService(
            StateMachineFactory<EvalMachine.States, EvalMachine.Events> stateMachineFactory,
            StateMachineRuntimePersister<EvalMachine.States, EvalMachine.Events, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<EvalMachine.States, EvalMachine.Events>(stateMachineFactory, stateMachineRuntimePersister);
    }

}
