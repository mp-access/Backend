package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;
import ch.uzh.ifi.access.student.evaluation.runner.SubmissionCodeRunner;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import com.spotify.docker.client.exceptions.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

public class DelegateCodeExecStep implements ProcessStep {

    private static final Logger logger = LoggerFactory.getLogger(DelegateCodeExecStep.class);

    private StudentSubmissionService submissionService;

    private CourseService courseService;

    private SubmissionCodeRunner codeRunner;

    @Autowired
    public DelegateCodeExecStep(StudentSubmissionService submissionService, CourseService courseService, SubmissionCodeRunner codeRunner) {
        this.submissionService = submissionService;
        this.courseService = courseService;
        this.codeRunner = codeRunner;
    }


    @Override
    public EvalMachine.Events execute(String submissionId) {

        Optional<StudentSubmission> opt = submissionService.findById(submissionId);
        if (opt.isPresent()) {
            if (opt.get() instanceof CodeSubmission) {

                CodeSubmission submission = (CodeSubmission) opt.get();
                Optional<Exercise> exOpt = courseService.getExerciseById(submission.getExerciseId());

                if (exOpt.isPresent()) {
                    try {

                        ExecResult execResult = codeRunner.execSubmissionForExercise(submission, exOpt.get());
                        submission.setConsole(execResult);
                        submissionService.saveSubmission(submission);

                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    } catch (DockerException e) {
                        logger.error(e.getMessage());
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    logger.warn("Submission without registered exercise found (submissionId:" + submissionId + ").");
                }


            } else {
                logger.warn("Unknown submission type for delegate step found (submissionId:" + submissionId + ").");
            }

        }

        return EvalMachine.Events.RETURN;
    }

}
