package ch.uzh.ifi.access.course.model;

public enum ExerciseType {
    singleChoice,
    multipleChoice,
    text,
    code,
    codeSnippet;

    public boolean isCodeType() {
        return code.equals(this) || codeSnippet.equals(this);
    }
}
