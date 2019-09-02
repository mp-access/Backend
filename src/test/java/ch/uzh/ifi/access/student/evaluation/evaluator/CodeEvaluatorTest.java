package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CodeEvaluatorTest {

    private Exercise exercise;
    private String errorTestLog;
    private String failsTestLog;
    private String okTestLog;

    @Before
    public void setUp() {
        exercise = Exercise.builder()
                .id("e1")
                .maxScore(10)
                .type(ExerciseType.code).build();

        errorTestLog = "runner/test/* (unittest.loader._FailedTest) ... ERROR\n" +
                "\n" +
                "======================================================================\n" +
                "ERROR: runner/test/* (unittest.loader._FailedTest)\n" +
                "----------------------------------------------------------------------\n" +
                "ImportError: Failed to import test module: runner/test/*\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/usr/lib/python3.7/unittest/loader.py\", line 154, in loadTestsFromName\n" +
                "    module = __import__(module_name)\n" +
                "ModuleNotFoundError: No module named 'runner/test/*'\n" +
                "\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 1 test in 0.001s\n" +
                "\n" +
                "FAILED (errors=1)\n";

        failsTestLog = "test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_upper (test.TestStringMethods1.TestStringMethods1) ... FAIL\n" +
                "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "test_upper (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: test_upper (test.TestStringMethods1.TestStringMethods1)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/home/mangoman/Workspace/MasterProject/CourseService/runner/test/TestStringMethods1.py\", line 6, in test_upper\n" +
                "    self.assertEqual('FOO'.upper(), 'Foo')\n" +
                "AssertionError: 'FOO' != 'Foo'\n" +
                "- FOO\n" +
                "+ Foo\n" +
                "\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 6 tests in 0.001s\n" +
                "\n" +
                "FAILED (failures=1)";

        okTestLog = "test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_upper (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "test_upper (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 6 tests in 0.001s\n" +
                "\n" +
                "OK\n";
    }

    @Test
    public void execWithErrors() {
        ExecResult console = new ExecResult();
        console.setEvalLog(errorTestLog);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(0.0, grade.getScore(), 0.25);
    }

    @Test
    public void execWithFailures() {
        ExecResult console = new ExecResult();
        console.setEvalLog(failsTestLog);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(5, grade.getPoints().getCorrect());
        Assert.assertEquals(8.25, grade.getScore(), 0.25);
    }

    @Test
    public void execOK() {
        ExecResult console = new ExecResult();
        console.setEvalLog(okTestLog);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(6, grade.getPoints().getCorrect());
        Assert.assertEquals(10.0, grade.getScore(), 0.25);
    }

}
