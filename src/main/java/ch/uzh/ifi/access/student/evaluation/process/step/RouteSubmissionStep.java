package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.EvalMachine;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class RouteSubmissionStep implements ProcessStep {

    private StudentSubmissionService submissionService;

    @Autowired
    public RouteSubmissionStep(StudentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    public EvalMachine.Events execute(String submissionId) {
        EvalMachine.Events resultingEvent = EvalMachine.Events.GRADE;

        Optional<StudentSubmission> opt = submissionService.findById(submissionId);
        if(opt.isPresent()){
            if (opt.get() instanceof CodeSubmission) {
                resultingEvent = EvalMachine.Events.DELEGATE;
            }
        }

        return resultingEvent;
    }

}
