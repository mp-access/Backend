package ch.uzh.ifi.access.student.evaluation;

public class EvalMachine {

    public enum States {
        SUBMITTED, DELEGATE, RETURNING, GRADING, FINISHED
    }

    public enum Events {
        GRADE, DELEGATE, RETURN, FINISH
    }

}
