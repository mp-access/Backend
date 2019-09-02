package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.student.evaluation.process.EvalMachine;

public interface ProcessStep {

    EvalMachine.Events execute(String submissionId);

}
