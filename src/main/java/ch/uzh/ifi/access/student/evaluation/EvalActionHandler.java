package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.evaluation.evaluator.TextEvaluator;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EvalActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EvalActionHandler.class);

    private HashMap<String, StudentSubmission> studentCtx;
    private HashMap<String, Exercise> exerciseCtx;

    public EvalActionHandler() {
        this.studentCtx = new HashMap<>();
        this.exerciseCtx = new HashMap<>();
    }

    public  Action<EvalMachine.States, EvalMachine.Events> submit() {
        return ctx -> {
            logger.warn("Submitting: {}", ctx);
            System.out.println("Submitting");
        };
    }

    public  Action<EvalMachine.States, EvalMachine.Events> grade() {
        return ctx -> {
            logger.warn("Grading: {}", ctx);

            StudentSubmission submission = loadSubmission(ctx);
            if(submission instanceof TextSubmission){
                Exercise exercise = getExercise(submission.getExerciseId());
                SubmissionEvaluation grad = new TextEvaluator().evaluate(submission, exercise);
                logger.debug("Graded result is: "+ grad.getScore());
                submission.setResult(grad);
                storeSubmission(submission);
            }

        };
    }

    public StudentSubmission loadSubmission(StateContext ctx) {
        String id =  ctx.getExtendedState().getVariables().getOrDefault("id", "").toString();
        logger.debug("submission id; "+ id);

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
