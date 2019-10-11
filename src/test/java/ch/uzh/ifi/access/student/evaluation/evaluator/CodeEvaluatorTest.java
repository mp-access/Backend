package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CodeEvaluatorTest {

    private Exercise exercise;
    private String errorTestLog;
    private String failsTestLog;
    private String hintsLog;
    private String hints2;
    private String okTestLog;
    private String hintsNotParsed;
    private String failLog;
    private String assertCountHint;
    private String assertListEqualHint;

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

        hintsLog = "test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n" +
                "test_upper (test.TestStringMethods1.TestStringMethods1) ... FAIL\n" +
                "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: test_upper (test.TestStringMethods1.TestStringMethods1)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/home/mangoman/Workspace/MasterProject/CourseService/runner/test/TestStringMethods1.py\", line 6, in test_upper\n" +
                "    self.assertEqual('FOO'.upper(), 'Foo')\n" +
                "AssertionError: 'FOO' != 'Foo'@@Erster Hinweis@@\n" +
                "- FOO\n" +
                "+ Foo\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 6 tests in 0.001s\n" +
                "\n" +
                "FAILED (failures=1)";

        hints2 = "test_doghouse (testSuite.Task2B) ... FAIL\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: test_doghouse (testSuite.Task2B)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/home/mangoman/Workspace/MasterProject/CourseStructure/assignment_01/exercise_04/private/testSuite.py\", line 29, in test_doghouse\n" +
                "    self.assertTrue(hasattr(self.exercise, \"dog\"), \"@@You must declare '{}'@@\".format(\"dog\"))\n" +
                "AssertionError: False is not true : @@You must declare 'dog'@@\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 1 test in 0.001s\n" +
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

        hintsNotParsed = "" +
                "........FF..\n" +
                "======================================================================\n" +
                "FAIL: test_case6 (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 53, in test_case6\n" +
                "    self._assert(\"abzAZ!\", 27, \"bcaBA!\", None)\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 32, in _assert\n" +
                "    self.assertEqual(expected, actual, msg)\n" +
                "AssertionError: 'bcaBA!' != 'abzAZ!'\n" +
                "- bcaBA!\n" +
                "+ abzAZ!\n" +
                " : @@ROT27 of 'abzAZ!' should be 'bcaBA!'\n" +
                ", but was 'abzAZ!'.@@\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: test_case7 (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 56, in test_case7\n" +
                "    self._assert(\"abzAZ!\", -27, \"zayZY!\", None)\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 32, in _assert\n" +
                "    self.assertEqual(expected, actual, msg)\n" +
                "AssertionError: 'zayZY!' != 'abzAZ!'\n" +
                "- zayZY!\n" +
                "+ abzAZ!\n" +
                " : @@ROT-27 of 'abzAZ!' should be 'zayZY!'\n" +
                ", but was 'abzAZ!'.@@\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 12 tests in 0.016s\n" +
                "\n" +
                "FAILED (failures=2)\n";

        failLog = "" +
                "F\n" +
                "======================================================================\n" +
                "FAIL: testFail (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n" +
                "    self.fail(m)\n" +
                "AssertionError: @@After the encoding, some letters have become non-letters.@@\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 1 test in 0.001s\n" +
                "\n" +
                "FAILED (failures=1)\n";

        assertCountHint = "" +
                "FF\n" +
                "======================================================================\n" +
                "FAIL: testFail (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n" +
                "    self.assertCountEqual([], [1], m)\n" +
                "AssertionError: Element counts were not equal:\n" +
                "First has 0, Second has 1:  1 : @@blablabla@@\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: testFail2 (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 88, in testFail2\n" +
                "    self.assertCountEqual([], [1], m)\n" +
                "AssertionError: Element counts were not equal:\n" +
                "First has 0, Second has 1:  1 : @@blablablablablabla@@\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 2 tests in 0.001s\n" +
                "\n" +
                "FAILED (failures=2)\n";

        assertListEqualHint = "" +
                "FF\n" +
                "======================================================================\n" +
                "FAIL: testFail (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n" +
                "    self.assertListEqual([], [1], m)\n" +
                "AssertionError: Lists differ: [] != [1]\n" +
                "\n" +
                "Second list contains 1 additional elements.\n" +
                "First extra element 0:\n" +
                "1\n" +
                "\n" +
                "- []\n" +
                "+ [1]\n" +
                "?  +\n" +
                " : @@blablabla@@\n" +
                "\n" +
                "======================================================================\n" +
                "FAIL: testFail2 (tests.PrivateTestSuite)\n" +
                "----------------------------------------------------------------------\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 88, in testFail2\n" +
                "    self.assertListEqual([], [1], m)\n" +
                "AssertionError: Lists differ: [] != [1]\n" +
                "\n" +
                "Second list contains 1 additional elements.\n" +
                "First extra element 0:\n" +
                "1\n" +
                "\n" +
                "- []\n" +
                "+ [1]\n" +
                "?  +\n" +
                " : @@blablablablablabla@@\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 2 tests in 0.001s\n" +
                "\n" +
                "FAILED (failures=2)\n";
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
        Assert.assertTrue(grade.getHints().isEmpty());
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

    @Test
    public void parseHints() {
        ExecResult console = new ExecResult();
        console.setEvalLog(hintsLog);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(5, grade.getPoints().getCorrect());
        Assert.assertEquals(1, grade.getHints().size());
    }

    @Test
    public void parseHints_OnlyAssertionErrorMsg() {
        ExecResult console = new ExecResult();
        console.setEvalLog(hints2);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(1, grade.getHints().size());
        Assert.assertEquals("You must declare 'dog'", grade.getHints().get(0));

    }

    @Test
    public void outOfMemoryHasEmptyEvalLog() {
        ExecResult console = new ExecResult();
        console.setEvalLog("");

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(0, grade.getPoints().getCorrect());
        Assert.assertEquals(exercise.getMaxScore(), grade.getMaxScore());
    }

    @Test
    public void nonsenseLog() {
        ExecResult console = new ExecResult();
        console.setEvalLog("asdfklajd blkasjd falsdjf  \n aljöflkjsd fasdf \n asdfjkl adsflkja sdf");

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);

        Assert.assertEquals(0, grade.getPoints().getCorrect());
        Assert.assertEquals(0.0, grade.getScore(), 0.1);
        Assert.assertEquals(0, grade.getHints().size());
    }

    @Test
    public void hintsNotParsed() {
        List<String> hints = new CodeEvaluator().parseHintsFromLog(hintsNotParsed);

        Assertions.assertThat(hints).size().isEqualTo(2);
        Assertions.assertThat(hints.get(0)).isEqualTo("ROT27 of 'abzAZ!' should be 'bcaBA!'\n, but was 'abzAZ!'.");
        Assertions.assertThat(hints.get(1)).isEqualTo("ROT-27 of 'abzAZ!' should be 'zayZY!'\n, but was 'abzAZ!'.");

        ExecResult console = new ExecResult();
        console.setEvalLog(hintsNotParsed);

        CodeSubmission sub = CodeSubmission.builder()
                .exerciseId(exercise.getId())
                .console(console)
                .build();

        hints = new CodeEvaluator().evaluate(sub, exercise).getHints();
        Assertions.assertThat(hints.get(0)).isEqualTo("ROT27 of 'abzAZ!' should be 'bcaBA!'\n, but was 'abzAZ!'.");
    }

    @Test
    public void failLogHint() {
        List<String> hints = new CodeEvaluator().parseHintsFromLog(failLog);

        Assertions.assertThat(hints).size().isEqualTo(1);
        Assertions.assertThat(hints.get(0)).isEqualTo("After the encoding, some letters have become non-letters.");
    }

    @Test
    public void assertCountHint() {
        List<String> hints = new CodeEvaluator().parseHintsFromLog(assertCountHint);

        Assertions.assertThat(hints).size().isEqualTo(2);
        Assertions.assertThat(hints.get(0)).isEqualTo("blablabla");
        Assertions.assertThat(hints.get(1)).isEqualTo("blablablablablabla");
    }

    @Test
    public void assertListEqualHint() {
        List<String> hints = new CodeEvaluator().parseHintsFromLog(assertListEqualHint);

        Assertions.assertThat(hints).size().isEqualTo(2);
        Assertions.assertThat(hints.get(0)).isEqualTo("blablabla");
        Assertions.assertThat(hints.get(1)).isEqualTo("blablablablablabla");
    }
}
