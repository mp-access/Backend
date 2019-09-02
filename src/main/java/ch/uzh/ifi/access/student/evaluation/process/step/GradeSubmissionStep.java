package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.evaluation.evaluator.*;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.model.*;
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

        Optional<StudentSubmission> optSub = submissionService.findById(submissionId);
        optSub.ifPresentOrElse(submission -> {
            logger.debug(String.format("Grade submission %s for exercise %s", submission.getId(), submission.getExerciseId()));


            if (submission.isGraded()) {
                Optional<Exercise> optEx = courseService.getExerciseById(submission.getExerciseId());
                optEx.ifPresent(exercise -> {
                    StudentSubmissionEvaluator evaluator = evaluator(submission);
                    if (evaluator != null) {
                        SubmissionEvaluation grade = evaluator.evaluate(submission, exercise);
                        logger.debug("Graded result is: " + grade.getScore());
                        submission.setResult(grade);
                        submissionService.saveSubmission(submission);
                    }
                });
            } else {
                logger.info("Submission not meant for grading ...");
            }
        }, () -> logger.error("No submission found (submissionId: " + submissionId + "),"));

        return EvalMachine.Events.FINISH;
    }

    StudentSubmissionEvaluator evaluator(StudentSubmission submission) {
        if (submission instanceof TextSubmission) {
            return new TextEvaluator();
        } else if (submission instanceof MultipleChoiceSubmission) {
            return new MultipleChoiceEvaluator();
        } else if (submission instanceof SingleChoiceSubmission) {
            return new SingleChoiceEvaluator();
        } else if (submission instanceof CodeSubmission) {
            return new CodeEvaluator();
        }

        logger.error("Unknown submission type for grading step found (submissionId: " + submission.getId() + ").");
        return null;
    }
}
