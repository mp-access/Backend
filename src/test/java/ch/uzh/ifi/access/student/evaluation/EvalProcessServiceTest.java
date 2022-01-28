package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineRepoService;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvalProcessServiceTest {

    private EvalMachineRepoService machineRepoService;

    private EvalProcessService processor;

    @BeforeEach
    public void setUp() {
        machineRepoService = new EvalMachineRepoService();

        processor = new EvalProcessService(null, machineRepoService);
    }

    @Test
    public void initProcessMachineCanBeFatchedFromRepo() {
        Exercise cex = buildCodeExerciseStub();
        CodeSubmission csub = buildCodeSubmissionStub(cex);

        String processId = processor.initEvalProcess(csub);

        Assertions.assertNotNull(machineRepoService.get(processId));
    }

    private Exercise buildCodeExerciseStub() {
        return Exercise.builder()
                .id("e1")
                .type(ExerciseType.code).build();
    }

    private CodeSubmission buildCodeSubmissionStub(Exercise ex) {
        return CodeSubmission.builder()
                .id("s1")
                .exerciseId(ex.getId())
                .build();
    }

}
