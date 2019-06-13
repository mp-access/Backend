package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.coderunner.CodeRunner;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.evaluation.evaluator.TextEvaluator;
import ch.uzh.ifi.access.student.evaluation.runner.SubmissionCodeRunner;
import ch.uzh.ifi.access.student.model.*;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class EvalActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EvalActionHandler.class);

    private HashMap<String, StudentSubmission> studentCtx;
    private HashMap<String, Exercise> exerciseCtx;
    private SubmissionCodeRunner codeRunner;

    public EvalActionHandler() throws DockerCertificateException {
        this.studentCtx = new HashMap<>();
        this.exerciseCtx = new HashMap<>();
        this.codeRunner = new SubmissionCodeRunner(new CodeRunner());
    }

    public Action<EvalMachine.States, EvalMachine.Events> redirectSubmission() {
        return ctx -> {
            logger.debug("Redirect submission: {}", ctx);

            StudentSubmission submission = loadSubmission(ctx);
            if (submission instanceof TextSubmission) {
                ctx.getStateMachine().sendEvent(EvalMachine.Events.GRADE);
            }else if(submission instanceof CodeSubmission) {
                ctx.getStateMachine().sendEvent(EvalMachine.Events.DELEGATE);
            }else{
                logger.warn("Cannot redirect! Unknown submission type found ... waiting for new events.");
            }
        };
    }

    public Action<EvalMachine.States, EvalMachine.Events> grade() {
        return ctx -> {
            logger.debug("Grading: {}", ctx);

            StudentSubmission submission = loadSubmission(ctx);
            if (submission instanceof TextSubmission) {
                Exercise exercise = getExercise(submission.getExerciseId());
                SubmissionEvaluation grad = new TextEvaluator().evaluate(submission, exercise);
                logger.debug("Graded result is: " + grad.getScore());
                submission.setResult(grad);
                storeSubmission(submission);
            }
        };
    }

    public Action<EvalMachine.States, EvalMachine.Events> delegateCodeExecution() {
        return ctx -> {
            logger.debug("Delegate code execution: {}", ctx);

            StudentSubmission submission = loadSubmission(ctx);
            if (submission instanceof CodeSubmission) {
                CodeSubmission codeSubmission = (CodeSubmission) submission;
                Exercise exercise = getExercise(submission.getExerciseId());

                try {

                    ExecResult execResult = codeRunner.execSubmissionForExercise(codeSubmission, exercise);
                    codeSubmission.setConsole(execResult);
                    storeSubmission(codeSubmission);

                    ctx.getStateMachine().sendEvent(EvalMachine.Events.RETURN);

                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                } catch (DockerException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            }else{
                logger.warn("Unknown submission type for delegate action found ... waiting for new events.");
            }
        };
    }

    public StudentSubmission loadSubmission(StateContext ctx) {
        String id = ctx.getExtendedState().getVariables().getOrDefault("id", "").toString();
        logger.debug("submission id; " + id);

        return studentCtx.get(id);
    }

    public Exercise getExercise(String exerciseId) {
        return exerciseCtx.get(exerciseId);
    }

    public void storeExercise(Exercise exercise) {
        exerciseCtx.put(exercise.getId(), exercise);
    }

    public StudentSubmission getSubmission(String submissionId) {
        return studentCtx.get(submissionId);
    }

    public void storeSubmission(StudentSubmission submission) {
        studentCtx.put(submission.getId(), submission);
    }



}
