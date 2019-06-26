package ch.uzh.ifi.access.student.evaluation.process;

import ch.uzh.ifi.access.course.service.CourseService;
import ch.uzh.ifi.access.student.evaluation.process.step.*;
import ch.uzh.ifi.access.student.evaluation.runner.SubmissionCodeRunner;
import ch.uzh.ifi.access.student.service.StudentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessStepFactoryService {

    private Map<String, ProcessStep> steps;

    @Autowired
    public ProcessStepFactoryService(StudentSubmissionService submissionService, CourseService courseService, SubmissionCodeRunner codeRunner) {
        steps = new HashMap<>();
        steps.put(DelegateCodeExecStep.class.getName(), new DelegateCodeExecStep(submissionService, courseService, codeRunner));
        steps.put(RouteSubmissionStep.class.getName(), new RouteSubmissionStep(submissionService));
        steps.put(GradeSubmissionStep.class.getName(), new GradeSubmissionStep(submissionService, courseService));
        steps.put(WaitForExecutedCodeStep.class.getName(), new WaitForExecutedCodeStep(submissionService));
    }

    public ProcessStep getStep(String key){
        return steps.get(key);
    }

}
