package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class RouteSubmissionStep implements ProcessStep {

    private static final Logger logger = LoggerFactory.getLogger(RouteSubmissionStep.class);

    private StudentSubmissionService submissionService;

    @Autowired
    public RouteSubmissionStep(StudentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    public EvalMachine.Events execute(String submissionId) {
        EvalMachine.Events resultingEvent = EvalMachine.Events.GRADE;

        Optional<StudentSubmission> opt = submissionService.findById(submissionId);
        if (opt.isPresent() && opt.get() instanceof CodeSubmission) {
            resultingEvent = EvalMachine.Events.DELEGATE;
        }

        logger.debug(String.format("Route submission %s to %s.", submissionId, resultingEvent));
        return resultingEvent;
    }

}
