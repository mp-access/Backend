package ch.uzh.ifi.access.student.evaluation;

public class EvalMachine {

    public enum States {
        SUBMITTED, DELEGATED, RETURNED, GRADED
    }

    public enum Events {
        GRADE, DELEGATE, RETURN
    }

}
