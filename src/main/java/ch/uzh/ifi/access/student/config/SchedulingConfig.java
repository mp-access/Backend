package ch.uzh.ifi.access.student.config;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineRepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    private static final long FIXED_DELAY_IN_MINUTES = 5;

    private static final long DOCKER_WATCHDOG_DELAY_IN_MINUTES = 5;

    private static final long DOCKER_IMAGE_UPDATER_DELAY_IN_MINUTES = 60;

    private final EvalMachineRepoService machineRepository;

    private final CodeRunner codeRunner;

    public SchedulingConfig(EvalMachineRepoService machineRepository, CodeRunner codeRunner) {
        this.machineRepository = machineRepository;
        this.codeRunner = codeRunner;
    }

    @Scheduled(fixedDelay = FIXED_DELAY_IN_MINUTES * 60000)
    public void cleanUpRepo() {
        Instant threshold = Instant.now().minus(5, ChronoUnit.MINUTES);
        logger.info("Starting state machine cleanup. Repository size {}, removing machine older than {}", machineRepository.count(), threshold);
        machineRepository.removeMachinesOlderThan(threshold);
        logger.info("Completed cleanup. Repository size {}", machineRepository.count());
    }

    @Scheduled(fixedDelay = DOCKER_WATCHDOG_DELAY_IN_MINUTES * 60000)
    public void dockerHealthCheck() {
        logger.info("Docker worker health check");
        codeRunner.logDockerInfo();
    }

    @Scheduled(fixedDelay = DOCKER_IMAGE_UPDATER_DELAY_IN_MINUTES * 60000)
    public void dockerImageUpdater() {
        logger.info("Docker worker health and image update check");
        codeRunner.check();
    }
}
