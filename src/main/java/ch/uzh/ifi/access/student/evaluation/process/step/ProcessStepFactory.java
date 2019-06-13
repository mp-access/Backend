package ch.uzh.ifi.access.student.evaluation.process.step;

import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.evaluation.runner.SubmissionCodeRunner;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessStepFactory {

    private Map<String, ProcessStep> steps;

    private StudentSubmissionService submissionService;
    private CourseService courseService;
    private SubmissionCodeRunner codeRunner;

    @Autowired
    public ProcessStepFactory(StudentSubmissionService submissionService, CourseService courseService, SubmissionCodeRunner codeRunner) {
        this.submissionService = submissionService;
        this.courseService = courseService;
        this.codeRunner = codeRunner;

        steps = new HashMap<>();
        steps.put(DelegateCodeExecStep.class.getName(), new DelegateCodeExecStep(submissionService, courseService, codeRunner));
        steps.put(RouteSubmissionStep.class.getName(), new RouteSubmissionStep(submissionService));
        steps.put(GradeSubmissionStep.class.getName(), new GradeSubmissionStep(submissionService, courseService));
    }

    public ProcessStep getStep(String key){
        return steps.get(key);
    }

}
