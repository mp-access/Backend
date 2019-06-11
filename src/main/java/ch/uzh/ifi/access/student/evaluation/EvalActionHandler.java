package ch.uzh.ifi.access.student.evaluation;

import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
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

    public EvalActionHandler() {
        this.studentCtx = new HashMap<>();
    }

    public  Action<EvalMachine.States, EvalMachine.Events> submit() {
        return ctx -> {
//            int approvals = (int) context.getExtendedState().getVariables()
//                    .getOrDefault("approvalCount", 0);
            logger.warn("Submitting: {}", ctx);
            System.out.println("Submitting");
        };
    }

    public  Action<EvalMachine.States, EvalMachine.Events> grade() {
        return ctx -> {
            logger.warn("Grading: {}", ctx);

            String id = extractSubmissionId(ctx);
            logger.debug("submission id; "+ id);
        };
    }

    private String extractSubmissionId(StateContext ctx) {
        return ctx.getExtendedState().getVariables().getOrDefault("id", "").toString();
    }
}
