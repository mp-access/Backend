package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachineRepoService;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EvalProcessServiceTest {

    private EvalMachineRepoService machineRepoService;

    private EvalProcessService processor;

    @Before
    public void setUp() {
        machineRepoService = new EvalMachineRepoService();

        processor = new EvalProcessService(null, machineRepoService);
    }

    @Test
    public void initProcessMachineCanBeFatchedFromRepo() {
        Exercise cex = buildCodeExerciseStub();
        CodeSubmission csub = buildCodeSubmissionStub(cex);

        String processId = processor.initEvalProcess(csub);

        Assertions.assertThat(machineRepoService.get(processId)).isNotNull();
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
