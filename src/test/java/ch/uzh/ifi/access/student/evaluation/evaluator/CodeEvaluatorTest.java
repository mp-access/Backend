package ch.uzh.ifi.access.student.evaluation.evaluator;

import static com.spotify.docker.client.shaded.com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;

public class CodeEvaluatorTest {

	private Exercise exercise;

	@Before
	public void setUp() {
		exercise = Exercise.builder().id("e1").maxScore(10).type(ExerciseType.code).build();

	}

	private SubmissionEvaluation evaluate(String output) {
		ExecResult console = new ExecResult();
		console.setEvalLog(output);

		CodeSubmission sub = CodeSubmission.builder().exerciseId(exercise.getId()).console(console).build();

		SubmissionEvaluation grade = new CodeEvaluator().evaluate(sub, exercise);
		return grade;
	}

	private static List<String> extractAllHints(String output) {
		List<String> hints = new CodeEvaluator().parseHintsFromLog(output);
		return hints;
	}

	private static List<String> hints(String... hints) {
		return newArrayList(hints);
	}

	@Test
	public void execWithErrors() {
		SubmissionEvaluation grade = evaluate("runner/test/* (unittest.loader._FailedTest) ... ERROR\n" + "\n"
				+ "======================================================================\n"
				+ "ERROR: runner/test/* (unittest.loader._FailedTest)\n"
				+ "----------------------------------------------------------------------\n"
				+ "ImportError: Failed to import test module: runner/test/*\n" + "Traceback (most recent call last):\n"
				+ "  File \"/usr/lib/python3.7/unittest/loader.py\", line 154, in loadTestsFromName\n"
				+ "    module = __import__(module_name)\n" + "ModuleNotFoundError: No module named 'runner/test/*'\n"
				+ "\n" + "\n" + "----------------------------------------------------------------------\n"
				+ "Ran 1 test in 0.001s\n" + "\n" + "FAILED (errors=1)\n");

		Assert.assertEquals(0.0, grade.getScore(), 0.25);
	}

	@Test
	public void execWithFailures() {
		SubmissionEvaluation grade = evaluate("test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_upper (test.TestStringMethods1.TestStringMethods1) ... FAIL\n"
				+ "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_upper (test.TestStringMethods2.TestStringMethods2) ... ok\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_upper (test.TestStringMethods1.TestStringMethods1)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/home/mangoman/Workspace/MasterProject/CourseService/runner/test/TestStringMethods1.py\", line 6, in test_upper\n"
				+ "    self.assertEqual('FOO'.upper(), 'Foo')\n" + "AssertionError: 'FOO' != 'Foo'\n" + "- FOO\n"
				+ "+ Foo\n" + "\n" + "\n" + "----------------------------------------------------------------------\n"
				+ "Ran 6 tests in 0.001s\n" + "\n" + "FAILED (failures=1)");

		Assert.assertEquals(5, grade.getPoints().getCorrect());
		Assert.assertEquals(8.25, grade.getScore(), 0.25);
		Assert.assertEquals(1, grade.getHints().size());
		Assert.assertEquals(hints(CodeEvaluator.TEST_FAILED_WITHOUT_HINTS), grade.getHints());
	}

	@Test
	public void execOK() {
		SubmissionEvaluation grade = evaluate("test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_upper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_upper (test.TestStringMethods2.TestStringMethods2) ... ok\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 6 tests in 0.001s\n"
				+ "\n" + "OK\n");

		Assert.assertEquals(6, grade.getPoints().getCorrect());
		Assert.assertEquals(10.0, grade.getScore(), 0.25);
	}

	@Test
	public void parseHints() {
		SubmissionEvaluation grade = evaluate("test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_upper (test.TestStringMethods1.TestStringMethods1) ... FAIL\n"
				+ "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_upper (test.TestStringMethods1.TestStringMethods1)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/home/mangoman/Workspace/MasterProject/CourseService/runner/test/TestStringMethods1.py\", line 6, in test_upper\n"
				+ "    self.assertEqual('FOO'.upper(), 'Foo')\n" + "AssertionError: 'FOO' != 'Foo'@@Erster Hinweis@@\n"
				+ "- FOO\n" + "+ Foo\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 6 tests in 0.001s\n"
				+ "\n" + "FAILED (failures=1)");

		Assert.assertEquals(5, grade.getPoints().getCorrect());
		Assert.assertEquals(1, grade.getHints().size());
	}

	@Test
	public void parseHints_OnlyAssertionErrorMsg() {
		SubmissionEvaluation grade = evaluate("test_doghouse (testSuite.Task2B) ... FAIL\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_doghouse (testSuite.Task2B)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/home/mangoman/Workspace/MasterProject/CourseStructure/assignment_01/exercise_04/private/testSuite.py\", line 29, in test_doghouse\n"
				+ "    self.assertTrue(hasattr(self.exercise, \"dog\"), \"@@You must declare '{}'@@\".format(\"dog\"))\n"
				+ "AssertionError: False is not true : @@You must declare 'dog'@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.001s\n"
				+ "\n" + "FAILED (failures=1)");

		Assert.assertEquals(1, grade.getHints().size());
		Assert.assertEquals(hints("You must declare 'dog'"), grade.getHints());

	}

	@Test
	public void outOfMemoryHasEmptyEvalLog() {
		SubmissionEvaluation grade = evaluate("");

		Assert.assertEquals(0, grade.getPoints().getCorrect());
		Assert.assertEquals(exercise.getMaxScore(), grade.getMaxScore());
	}

	@Test
	public void nonsenseLog() {
		SubmissionEvaluation grade = evaluate("asdfklajd blkasjd falsdjf  \n aljöflkjsd fasdf \n asdfjkl adsflkja sdf");

		Assert.assertEquals(0, grade.getPoints().getCorrect());
		Assert.assertEquals(0.0, grade.getScore(), 0.1);
		Assert.assertEquals(hints(), grade.getHints());
	}

	@Test
	public void hintsNotParsed() {
		String output = "........FF..\n" + "======================================================================\n"
				+ "FAIL: test_case6 (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 53, in test_case6\n"
				+ "    self._assert(\"abzAZ!\", 27, \"bcaBA!\", None)\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 32, in _assert\n"
				+ "    self.assertEqual(expected, actual, msg)\n" + "AssertionError: 'bcaBA!' != 'abzAZ!'\n"
				+ "- bcaBA!\n" + "+ abzAZ!\n" + " : @@ROT27 of 'abzAZ!' should be 'bcaBA!'\n"
				+ ", but was 'abzAZ!'.@@\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_case7 (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 56, in test_case7\n"
				+ "    self._assert(\"abzAZ!\", -27, \"zayZY!\", None)\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 32, in _assert\n"
				+ "    self.assertEqual(expected, actual, msg)\n" + "AssertionError: 'zayZY!' != 'abzAZ!'\n"
				+ "- zayZY!\n" + "+ abzAZ!\n" + " : @@ROT-27 of 'abzAZ!' should be 'zayZY!'\n"
				+ ", but was 'abzAZ!'.@@\n" + "\n"
				+ "----------------------------------------------------------------------\n"
				+ "Ran 12 tests in 0.016s\n" + "\n" + "FAILED (failures=2)\n";

		List<String> actuals = extractAllHints(output);
		List<String> expecteds = hints("ROT27 of 'abzAZ!' should be 'bcaBA!'\n, but was 'abzAZ!'.",
				"ROT-27 of 'abzAZ!' should be 'zayZY!'\n, but was 'abzAZ!'.");
		assertEquals(expecteds, actuals);

		actuals = evaluate(output).getHints();
		expecteds = hints("ROT27 of 'abzAZ!' should be 'bcaBA!'\n, but was 'abzAZ!'.");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void failLogHint() {
		List<String> actuals = extractAllHints(
				"F\n" + "======================================================================\n"
						+ "FAIL: testFail (tests.PrivateTestSuite)\n"
						+ "----------------------------------------------------------------------\n"
						+ "Traceback (most recent call last):\n"
						+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n"
						+ "    self.fail(m)\n"
						+ "AssertionError: @@After the encoding, some letters have become non-letters.@@\n" + "\n"
						+ "----------------------------------------------------------------------\n"
						+ "Ran 1 test in 0.001s\n" + "\n" + "FAILED (failures=1)\n");
		List<String> expecteds = hints("After the encoding, some letters have become non-letters.");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void assertCountHint() {
		List<String> actuals = extractAllHints("FF\n"
				+ "======================================================================\n"
				+ "FAIL: testFail (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n"
				+ "    self.assertCountEqual([], [1], m)\n" + "AssertionError: Element counts were not equal:\n"
				+ "First has 0, Second has 1:  1 : @@blablabla@@\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: testFail2 (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 88, in testFail2\n"
				+ "    self.assertCountEqual([], [1], m)\n" + "AssertionError: Element counts were not equal:\n"
				+ "First has 0, Second has 1:  1 : @@blablablablablabla@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 2 tests in 0.001s\n"
				+ "\n" + "FAILED (failures=2)\n");
		List<String> expecteds = hints("blablabla", "blablablablablabla");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void assertListEqualHint() {
		List<String> actuals = extractAllHints("FF\n"
				+ "======================================================================\n"
				+ "FAIL: testFail (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 84, in testFail\n"
				+ "    self.assertListEqual([], [1], m)\n" + "AssertionError: Lists differ: [] != [1]\n" + "\n"
				+ "Second list contains 1 additional elements.\n" + "First extra element 0:\n" + "1\n" + "\n" + "- []\n"
				+ "+ [1]\n" + "?  +\n" + " : @@blablabla@@\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: testFail2 (tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 88, in testFail2\n"
				+ "    self.assertListEqual([], [1], m)\n" + "AssertionError: Lists differ: [] != [1]\n" + "\n"
				+ "Second list contains 1 additional elements.\n" + "First extra element 0:\n" + "1\n" + "\n" + "- []\n"
				+ "+ [1]\n" + "?  +\n" + " : @@blablablablablabla@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 2 tests in 0.001s\n"
				+ "\n" + "FAILED (failures=2)\n");
		List<String> expecteds = hints("blablabla", "blablablablablabla");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void crashedLogHint() {
		List<String> actuals = extractAllHints("E\n"
				+ "======================================================================\n"
				+ "ERROR: tests (unittest.loader._FailedTest)\n"
				+ "----------------------------------------------------------------------\n"
				+ "ImportError: Failed to import test module: tests\n" + "Traceback (most recent call last):\n"
				+ "  File \"//anaconda3/lib/python3.7/unittest/loader.py\", line 436, in _find_test_path\n"
				+ "    module = self._get_module_from_name(name)\n"
				+ "  File \"//anaconda3/lib/python3.7/unittest/loader.py\", line 377, in _get_module_from_name\n"
				+ "    __import__(name)\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/private/tests.py\", line 17, in <module>\n"
				+ "    from public import script\n"
				+ "  File \"/Users/alexhofmann/Downloads/exercise_03/public/script.py\", line 14\n"
				+ "    from string import ascii_lowercase as lc, ascii_uppercase as uc\n" + "       ^\n"
				+ "IndentationError: expected an indented block\n" + "\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.000s\n"
				+ "\n" + "FAILED (errors=1)\n");
		List<String> expecteds = hints("IndentationError");
		assertEquals(expecteds, actuals);
	}
}
