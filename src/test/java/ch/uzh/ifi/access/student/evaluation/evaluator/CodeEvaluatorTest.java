package ch.uzh.ifi.access.student.evaluation.evaluator;

import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.ExecResult;
import ch.uzh.ifi.access.student.model.SubmissionEvaluation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static ch.uzh.ifi.access.student.evaluation.evaluator.CodeEvaluator.TEST_FAILED_WITHOUT_HINTS;
import static com.spotify.docker.client.shaded.com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

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

		assertEquals(0.0, grade.getScore(), 0.25);
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

		assertEquals(5, grade.getPoints().getCorrect());
		assertEquals(8.25, grade.getScore(), 0.25);
		assertEquals(hints(TEST_FAILED_WITHOUT_HINTS), grade.getHints());
	}

	@Test
	public void execWithFailuresAndErrors() {
		SubmissionEvaluation grade = evaluate("test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_upper (test.TestStringMethods1.TestStringMethods1) ... FAIL\n"
				+ "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_upper (test.TestStringMethods2.TestStringMethods2) ... ERROR\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_upper (test.TestStringMethods1.TestStringMethods1)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/home/mangoman/Workspace/MasterProject/CourseService/runner/test/TestStringMethods1.py\", line 6, in test_upper\n"
				+ "    self.assertEqual('FOO'.upper(), 'Foo')\n" + "AssertionError: 'FOO' != 'Foo'\n" + "- FOO\n"
				+ "+ Foo\n" + "\n" + "\n" + "----------------------------------------------------------------------\n"
				+ "Ran 6 tests in 0.001s\n" + "\n" + "FAILED (failures=1, errors=1)");

		assertEquals(4, grade.getPoints().getCorrect());
		assertEquals(6.75, grade.getScore(), 0.25);
		assertEquals(hints(TEST_FAILED_WITHOUT_HINTS), grade.getHints());
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

		assertEquals(6, grade.getPoints().getCorrect());
		assertEquals(10.0, grade.getScore(), 0.25);
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

		assertEquals(5, grade.getPoints().getCorrect());
		assertEquals(hints("Erster Hinweis"), grade.getHints());
	}

	@Test
	public void parseHintsOnlyAssertionErrorMsg() {
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

		assertEquals(hints("You must declare 'dog'"), grade.getHints());

	}

	@Test
	public void outOfMemoryHasEmptyEvalLog() {
		SubmissionEvaluation grade = evaluate("");

		assertEquals(0, grade.getPoints().getCorrect());
		assertEquals(exercise.getMaxScore(), grade.getMaxScore(), 0.1);
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
				+ "Ran 10 tests in 0.016s\n" + "\n" + "FAILED (failures=2)\n";

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
	public void errorDueToIndentation() {
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
		List<String> expecteds = hints("Error during execution: IndentationError");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void extractionWorksWithMultipleHints() {
		List<String> actuals = extractAllHints("FF\n"
				+ "======================================================================\n"
				+ "FAIL: test_1 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 52, in test_1\n"
				+ "    self._assert([], [], [], \"empty\")\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 41, in _assert\n"
				+ "    self._assertType(list, actual)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 23, in _assertType\n"
				+ "    self.fail(m)\n" + "AssertionError: @@First hint@@\n" + "\n"
				+ "======================================================================\n"
				+ "FAIL: test_2 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 55, in test_2\n"
				+ "    self._assert([1], [2], [(1, 2)])\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 41, in _assert\n"
				+ "    self._assertType(list, actual)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 23, in _assertType\n"
				+ "    self.fail(m)\n" + "AssertionError: @@second hint@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 2 tests in 0.001s\n"
				+ "\n" + "FAILED (failures=2)");
		List<String> expecteds = hints("First hint", "second hint");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void importError() {
		List<String> actuals = extractAllHints("F\n"
				+ "======================================================================\n"
				+ "FAIL: test_1 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 52, in test_1\n"
				+ "    self._assert([], [], [], \"empty\")\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 30, in _assert\n"
				+ "    self.fail(m)\n" + "AssertionError: @@Could not import solution for testing: NameError@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.000s\n"
				+ "\n" + "FAILED (failures=1)");
		List<String> expecteds = hints("Could not import solution for testing: NameError");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void executionError() {
		List<String> actuals = extractAllHints("F\n"
				+ "======================================================================\n"
				+ "FAIL: test_1 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 34, in _assert\n"
				+ "    actual = merge(a, b)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/public/script.py\", line 4, in merge\n"
				+ "    xxx\n" + "NameError: name 'xxx' is not defined\n" + "\n"
				+ "During handling of the above exception, another exception occurred:\n" + "\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 52, in test_1\n"
				+ "    self._assert([], [], [], \"empty\")\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 38, in _assert\n"
				+ "    self.fail(m)\n" + "AssertionError: @@Could not execute the solution for testing: NameError@@\n"
				+ "\n" + "----------------------------------------------------------------------\n"
				+ "Ran 1 test in 0.000s\n" + "\n" + "FAILED (failures=1)");
		List<String> expecteds = hints("Could not execute the solution for testing: NameError");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void assertEqualDists() {
		List<String> actuals = extractAllHints("F\n"
				+ "======================================================================\n"
				+ "FAIL: test_1 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 52, in test_1\n"
				+ "    self._assert([], [], [], \"@@Empty lists are not handled correctly.@@\")\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_01_merge_lists/private/tests.py\", line 48, in _assert\n"
				+ "    self.assertEqual(expected, actual, m)\n" + "AssertionError: Lists differ: [] != ['a']\n" + "\n"
				+ "Second list contains 1 additional elements.\n" + "First extra element 0:\n" + "'a'\n" + "\n"
				+ "- []\n" + "+ ['a'] : @@Empty lists are not handled correctly.@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.000s\n"
				+ "\n" + "FAILED (failures=1)");
		List<String> expecteds = hints("Empty lists are not handled correctly.");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void assertEqualDicts() {
		List<String> actuals = extractAllHints("F\n"
				+ "======================================================================\n"
				+ "FAIL: test_2 (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_02_invert_dict/private/tests.py\", line 59, in test_2\n"
				+ "    self._assert({1:2}, {2:[1]})\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_02_invert_dict/private/tests.py\", line 52, in _assert\n"
				+ "    self.assertEqual(expected, actual, m)\n" + "AssertionError: {2: [1]} != {}\n" + "- {2: [1]}\n"
				+ "+ {} : @@Result is incorrect for input {1: 2}.@@\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.001s\n"
				+ "\n" + "FAILED (failures=1)");
		List<String> expecteds = hints("Result is incorrect for input {1: 2}.");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void errorDuringImport() {
		List<String> actuals = extractAllHints("Traceback (most recent call last):\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/runpy.py\", line 193, in _run_module_as_main\n"
				+ "    \"__main__\", mod_spec)\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/runpy.py\", line 85, in _run_code\n"
				+ "    exec(code, run_globals)\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/__main__.py\", line 18, in <module>\n"
				+ "    main(module=None)\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/main.py\", line 94, in __init__\n"
				+ "    self.parseArgs(argv)\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/main.py\", line 141, in parseArgs\n"
				+ "    self.createTests()\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/main.py\", line 148, in createTests\n"
				+ "    self.module)\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/loader.py\", line 219, in loadTestsFromNames\n"
				+ "    suites = [self.loadTestsFromName(name, module) for name in names]\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/loader.py\", line 219, in <listcomp>\n"
				+ "    suites = [self.loadTestsFromName(name, module) for name in names]\n"
				+ "  File \"/opt/local/Library/Frameworks/Python.framework/Versions/3.6/lib/python3.6/unittest/loader.py\", line 153, in loadTestsFromName\n"
				+ "    module = __import__(module_name)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/private/tests.py\", line 12, in <module>\n"
				+ "    from public.script import analyze\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/public/script.py\", line 3, in <module>\n"
				+ "    xxx\n" + "NameError: name 'xxx' is not defined");
		List<String> expecteds = hints("Error during import: NameError");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void errorDuringExecution() {
		List<String> actuals = extractAllHints("E\n"
				+ "======================================================================\n"
				+ "ERROR: test01_empty_list (private.tests.PrivateTestSuite)\n"
				+ "----------------------------------------------------------------------\n"
				+ "Traceback (most recent call last):\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/private/tests.py\", line 60, in test01_empty_list\n"
				+ "    self._assert([], {}, \"@@The result is not correct for an empty list of posts.@@\")\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/private/tests.py\", line 42, in _assert\n"
				+ "    actual = self._exec(_in)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/private/tests.py\", line 29, in _exec\n"
				+ "    return analyze(_in)\n"
				+ "  File \"/Users/seb/versioned/access/access-playground-staging-tutors/assignment_05/exercise_03_count_hashtags/public/script.py\", line 4, in analyze\n"
				+ "    xxx\n" + "NameError: name 'xxx' is not defined\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 1 test in 0.001s\n"
				+ "\n" + "FAILED (errors=1)");
		List<String> expecteds = hints("Error during execution: NameError");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void noPointsErrorUnspecified() {
		List<String> actuals = extractAllHints("Some output, no ok, no testing...");
		List<String> expecteds = hints(
				"No hint could be provided. This is likely caused by a crash during the execution.");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void maxPointsNoError() {

		String in = "test_isupper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_split (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_upper (test.TestStringMethods1.TestStringMethods1) ... ok\n"
				+ "test_isupper (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_split (test.TestStringMethods2.TestStringMethods2) ... ok\n"
				+ "test_upper (test.TestStringMethods2.TestStringMethods2) ... ok\n" + "\n"
				+ "----------------------------------------------------------------------\n" + "Ran 6 tests in 0.001s\n"
				+ "\n" + "OK\n";

		List<String> actuals = extractAllHints(in);
		List<String> expecteds = hints(
				"No hint could be provided. This is likely caused by a crash during the execution.");
		assertEquals(expecteds, actuals);

		actuals = evaluate(in).getHints();
		expecteds = hints();
		assertEquals(expecteds, actuals);
	}

	@Test
	public void maxiTest() {
		String in = "FFFFFFFFFFFFFFFFFFF\n" +
				"======================================================================\n" +
				"FAIL: test_assertAlmostEqual (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 62, in test_assertAlmostEqual\n" +
				"    self.assertAlmostEqual(0.123, 0.124, delta=0.0001, msg=m(\"assertAlmostEqual\"))\n" +
				"AssertionError: 0.123 != 0.124 within 0.0001 delta : @@assertAlmostEqual@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertDictEqual (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 27, in test_assertDictEqual\n" +
				"    self.assertDictEqual({\"a\":1}, {\"b\":1}, m(\"assertDictEqual\"))\n" +
				"AssertionError: {'a': 1} != {'b': 1}\n" +
				"- {'a': 1}\n" +
				"?   ^\n" +
				"+ {'b': 1}\n" +
				"?   ^\n" +
				" : @@assertDictEqual@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertFalse (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 37, in test_assertFalse\n" +
				"    self.assertFalse(True, m(\"assertFalse\"))\n" +
				"AssertionError: True is not false : @@assertFalse@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertIs (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 43, in test_assertIs\n" +
				"    self.assertIs(\"a\", \"b\", m(\"assertIs\"))\n" +
				"AssertionError: 'a' is not 'b' : @@assertIs@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertIsInstance (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 56, in test_assertIsInstance\n" +
				"    self.assertIsInstance(1, str, m(\"assertIsInstance\"))\n" +
				"AssertionError: 1 is not an instance of <class 'str'> : @@assertIsInstance@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertIsNone (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 50, in test_assertIsNone\n" +
				"    self.assertIsNone(\"\", m(\"assertIsNone\"))\n" +
				"AssertionError: '' is not None : @@assertIsNone@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertIsNot (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 47, in test_assertIsNot\n" +
				"    self.assertIsNot(a, a, m(\"assertIsNot\"))\n" +
				"AssertionError: unexpectedly identical: 'a' : @@assertIsNot@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertIsNotNone (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 53, in test_assertIsNotNone\n" +
				"    self.assertIsNotNone(None, m(\"assertIsNotNone\"))\n" +
				"AssertionError: unexpectedly None : @@assertIsNotNone@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertListEqual (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 24, in test_assertListEqual\n" +
				"    self.assertListEqual([0], [1], m(\"assertListEqual\"))\n" +
				"AssertionError: Lists differ: [0] != [1]\n" +
				"First differing element 0:\n" +
				"0\n" +
				"1\n" +
				"- [0]\n" +
				"+ [1] : @@assertListEqual@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertNotAlmostEqual (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 65, in test_assertNotAlmostEqual\n" +
				"    self.assertNotAlmostEqual(0.123, 0.123, delta=0.0001, msg=m(\"assertNotAlmostEqual\"))\n" +
				"AssertionError: 0.123 == 0.123 within 0.0001 delta : @@assertNotAlmostEqual@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertNotIsInstance (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 59, in test_assertNotIsInstance\n" +
				"    self.assertNotIsInstance(\"\", str, m(\"assertNotIsInstance\"))\n" +
				"AssertionError: '' is an instance of <class 'str'> : @@assertNotIsInstance@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertRaises (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 34, in test_assertRaises\n" +
				"    nonCrashingStatement = 1234\n" +
				"AssertionError: Warning not raised : @@assertRaises@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertTrue (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 40, in test_assertTrue\n" +
				"    self.assertTrue(False, m(\"assertTrue\"))\n" +
				"AssertionError: False is not true : @@assertTrue@@\n" +
				"======================================================================\n" +
				"FAIL: test_assertTupleEqual (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 21, in test_assertTupleEqual\n" +
				"    self.assertTupleEqual((0,), (1,), m(\"assertTupleEqual\"))\n" +
				"AssertionError: Tuples differ: (0,) != (1,)\n" +
				"First differing element 0:\n" +
				"0\n" +
				"1\n" +
				"- (0,)\n" +
				"?  ^\n" +
				"+ (1,)\n" +
				"?  ^\n" +
				" : @@assertTupleEqual@@\n" +
				"======================================================================\n" +
				"FAIL: test_assert_equal_dict (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 18, in test_assert_equal_dict\n" +
				"    self.assertEqual([{\"a\":1}], [{\"b\":1}], m(\"assertEqual (dict)\"))\n" +
				"AssertionError: Lists differ: [{'a': 1}] != [{'b': 1}]\n" +
				"First differing element 0:\n" +
				"{'a': 1}\n" +
				"{'b': 1}\n" +
				"- [{'a': 1}]\n" +
				"?    ^\n" +
				"+ [{'b': 1}]\n" +
				"?    ^\n" +
				" : @@assertEqual (dict)@@\n" +
				"======================================================================\n" +
				"FAIL: test_assert_equal_int (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 9, in test_assert_equal_int\n" +
				"    self.assertEqual(0, 1, m(\"assertEqual (int)\"))\n" +
				"AssertionError: 0 != 1 : @@assertEqual (int)@@\n" +
				"======================================================================\n" +
				"FAIL: test_assert_equal_list (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 15, in test_assert_equal_list\n" +
				"    self.assertEqual([0], [1], m(\"assertEqual (list)\"))\n" +
				"AssertionError: Lists differ: [0] != [1]\n" +
				"First differing element 0:\n" +
				"0\n" +
				"1\n" +
				"- [0]\n" +
				"+ [1] : @@assertEqual (list)@@\n" +
				"======================================================================\n" +
				"FAIL: test_assert_equal_tuple (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 12, in test_assert_equal_tuple\n" +
				"    self.assertEqual((0,), (1,), m(\"assertEqual (tuple)\"))\n" +
				"AssertionError: Tuples differ: (0,) != (1,)\n" +
				"First differing element 0:\n" +
				"0\n" +
				"1\n" +
				"- (0,)\n" +
				"?  ^\n" +
				"+ (1,)\n" +
				"?  ^\n" +
				" : @@assertEqual (tuple)@@\n" +
				"======================================================================\n" +
				"FAIL: test_fail (test_example.ExampleTest)\n" +
				"----------------------------------------------------------------------\n" +
				"Traceback (most recent call last):\n" +
				"  File \"/Users/seb/Desktop/test_example.py\", line 30, in test_fail\n" +
				"    self.fail(m(\"fail\"))\n" +
				"AssertionError: @@fail@@\n" +
				"----------------------------------------------------------------------\n" +
				"Ran 19 tests in 0.003s\n" +
				"FAILED (failures=19)\n";
		List<String> actuals = extractAllHints(in);
		List<String> expecteds = hints("assertAlmostEqual", "assertDictEqual", "assertFalse", "assertIs", "assertIsInstance", "assertIsNone", "assertIsNot", "assertIsNotNone", "assertListEqual", "assertNotAlmostEqual", "assertNotIsInstance", "assertRaises", "assertTrue", "assertTupleEqual", "assertEqual (dict)", "assertEqual (int)", "assertEqual (list)", "assertEqual (tuple)", "fail");

		assertEquals(expecteds, actuals);
	}
}
