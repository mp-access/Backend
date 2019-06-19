package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class WaitForExecutedCodeStep implements ProcessStep {

    private static final Logger logger = LoggerFactory.getLogger(WaitForExecutedCodeStep.class);

    private final StudentSubmissionService submissionService;

    public WaitForExecutedCodeStep(StudentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    public EvalMachine.Events execute(String submissionId) {

        Optional<StudentSubmission> opt = submissionService.findById(submissionId);
        if (opt.isPresent() && opt.get() instanceof CodeSubmission) {
            CodeSubmission submission = (CodeSubmission) opt.get();
            if(submission.getConsole() == null || (submission.getConsole().getStdout() == null && submission.getConsole().getStderr() == null)){
                // Submission has no execution result yet --> wait.
                logger.debug(String.format("Wait a bit ... no execution result found for submission %s.", submissionId));
                return EvalMachine.Events.RETURN;
            }
        }

        // No code submission or submission with execution result --> forward for grading.
        logger.debug(String.format("Forward submission %s to grading.", submissionId));
        return EvalMachine.Events.GRADE;
    }

}
