package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.evaluator.CodeEvaluator;
import ch.uzh.ifi.access.student.evaluation.evaluator.TextEvaluator;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class GradeSubmissionStep implements ProcessStep {

    private static final Logger logger = LoggerFactory.getLogger(DelegateCodeExecStep.class);

    private StudentSubmissionService submissionService;

    private CourseService courseService;

    @Autowired
    public GradeSubmissionStep(StudentSubmissionService submissionService, CourseService courseService) {
        this.submissionService = submissionService;
        this.courseService = courseService;
    }

    @Override
    public EvalMachine.Events execute(String submissionId) {

        Optional<StudentSubmission> opt = submissionService.findById(submissionId);
        if (opt.isPresent()) {

            StudentSubmission submission = opt.get();
            Optional<Exercise> exOpt = courseService.getExerciseById(submission.getExerciseId());

            if (exOpt.isPresent()) {
                Exercise exercise = exOpt.get();

                if (submission instanceof TextSubmission) {
                    SubmissionEvaluation grad = new TextEvaluator().evaluate(submission, exercise);
                    logger.debug("Graded result is: " + grad.getScore());
                    submission.setResult(grad);
                    submissionService.saveSubmission(submission);
                }else if(submission instanceof CodeSubmission){
                    SubmissionEvaluation grad = new CodeEvaluator().evaluate(submission, exercise);
                    logger.debug("Graded result is: " + grad.getScore());
                    submission.setResult(grad);
                    submissionService.saveSubmission(submission);
                }else {
                    logger.warn("Unknown submission type for grading step found (submissionId:" + submissionId + ").");
                }
            }
        }

        return EvalMachine.Events.FINISH;
    }
}
