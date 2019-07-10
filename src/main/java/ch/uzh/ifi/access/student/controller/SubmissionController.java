package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.controller.ResourceNotFoundException;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.dto.StudentAnswerDTO;
import ch.uzh.ifi.access.student.dto.SubmissionHistoryDTO;
import ch.uzh.ifi.access.student.dto.SubmissionResult;
import ch.uzh.ifi.access.student.evaluation.EvalProcessService;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final static Logger logger = LoggerFactory.getLogger(SubmissionController.class);

    private final StudentSubmissionService studentSubmissionService;

    private final CourseService courseService;

    private final EvalProcessService processService;

    public SubmissionController(StudentSubmissionService studentSubmissionService, CourseService courseService, EvalProcessService processService) {
        this.studentSubmissionService = studentSubmissionService;
        this.courseService = courseService;
        this.processService = processService;
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<StudentSubmission> getSubmissionById(@PathVariable String submissionId, @ApiIgnore CourseAuthentication authentication) {
        StudentSubmission submission = studentSubmissionService.findById(submissionId).orElse(null);

        if (submission == null || !submission.userIdMatches(authentication.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(submission);
    }

    @GetMapping("/exercises/{exerciseId}")
    public StudentSubmission getSubmissionByExercise(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        String username = authentication.getName();
        String userId = authentication.getUserId();

        logger.info(String.format("Fetching submission for user %s", username));

        return studentSubmissionService
                .findLatestExerciseSubmission(exerciseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cannot find any submission for user %s and exercise %s", userId, exerciseId)));
    }

    @PostMapping("/exs/{exerciseId}")
    public Map.Entry<String, String> submitEval(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, @ApiIgnore CourseAuthentication authentication) throws InterruptedException {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        Optional<String> commitHash = courseService.getExerciseById(exerciseId).map(Exercise::getGitHash);
        String processId = "N/A";
        if (commitHash.isPresent()) {
            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash.get());
            submission = studentSubmissionService.initSubmission(submission);
            processId = processService.initEvalProcess(submission);
            processService.fireEvalProcessExecutionAsync(processId);
        }
        return new AbstractMap.SimpleEntry("evalId", processId);
    }

    @GetMapping("/evals/{processId}")
    public Map<String, String>  getEvalProcessState(@PathVariable String processId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");
        Assert.notNull(processId, "No processId.");
        return processService.getEvalProcessState(processId);
    }

    @PostMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> submitExercise(@PathVariable String exerciseId, @RequestBody StudentAnswerDTO submissionDTO, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        String username = authentication.getName();

        logger.info(String.format("User %s submitted exercise: %s", username, exerciseId));

        Optional<String> commitHash = courseService.getExerciseById(exerciseId).map(Exercise::getGitHash);

        if (commitHash.isPresent()) {
            StudentSubmission submission = submissionDTO.createSubmission(authentication.getUserId(), exerciseId, commitHash.get());
            return ResponseEntity.accepted().body(studentSubmissionService.initSubmission(submission));
        } else {
            return ResponseEntity.badRequest().body("Referenced exercise does not exist");
        }
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public SubmissionHistoryDTO getAllSubmissionsForExercise(@PathVariable String exerciseId, @ApiIgnore CourseAuthentication authentication) {
        Assert.notNull(authentication, "No authentication object found for user");

        logger.info(String.format("Fetching all submission for user %s and exercise %s", authentication.getName(), exerciseId));

        List<StudentSubmission> submissions = studentSubmissionService.findAllSubmissionsByExerciseAndUserOrderedByVersionDesc(exerciseId, authentication.getUserId());
        return new SubmissionHistoryDTO(submissions);
    }

    /**
     * Just for testing
     **/
    @GetMapping("/{submissionId}/logs")
    public SubmissionResult getSubmissionLogs(@PathVariable String submissionId) {
        return new SubmissionResult(submissionId, randomLogOutputForTesting());
    }

    private String randomLogOutputForTesting() {
        final String success = "python3 -m unittest testSuite.py -v\n" +
                "test_aircraft_get_name (testSuite.Task1Test) ... ok\n" +
                "test_aircraft_get_number_of_passengers (testSuite.Task1Test) ... ok\n" +
                "test_calculate_amount_of_fuel_intercontinental (testSuite.Task1Test) ... ok\n" +
                "test_calculate_amount_of_fuel_short_haul (testSuite.Task1Test) ... ok\n" +
                "test_get_manifest_intercontinental (testSuite.Task1Test) ... ok\n" +
                "test_get_manifest_short_haul (testSuite.Task1Test) ... ok\n" +
                "test_inheritance (testSuite.Task1Test) ... ok\n" +
                "test_list_flights (testSuite.Task1Test) ... ok\n" +
                "\n" +
                "----------------------------------------------------------------------\n" +
                "Ran 8 tests in 0.000s\n" +
                "\n" +
                "OK\n";

        final String failure = "python -m unittest testSuite.py -v\n" +
                "Traceback (most recent call last):\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/runpy.py\", line 162, in _run_module_as_main\n" +
                "    \"__main__\", fname, loader, pkg_name)\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/runpy.py\", line 72, in _run_code\n" +
                "    exec code in run_globals\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/__main__.py\", line 12, in <module>\n" +
                "    main(module=None)\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/main.py\", line 94, in __init__\n" +
                "    self.parseArgs(argv)\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/main.py\", line 149, in parseArgs\n" +
                "    self.createTests()\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/main.py\", line 158, in createTests\n" +
                "    self.module)\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/loader.py\", line 130, in loadTestsFromNames\n" +
                "    suites = [self.loadTestsFromName(name, module) for name in names]\n" +
                "  File \"/System/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/unittest/loader.py\", line 91, in loadTestsFromName\n" +
                "    module = __import__('.'.join(parts_copy))\n" +
                "  File \"testSuite.py\", line 38\n" +
                "    f\"Intercontinental flight intercontinental: passenger count 40, cargo load 100\")\n" +
                "                                                                                  ^\n" +
                "SyntaxError: invalid syntax";

        return new Random().nextBoolean() ? success : failure;
    }
}
