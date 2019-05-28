package ch.uzh.ifi.access.course.model.workspace;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.VirtualFile;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class WorkspaceTest {

    @Test
    public void workspace() {
        VirtualFile vf1 = TestObjectFactory.createVirtualFile("test", "py", false);
        VirtualFile vf2 = TestObjectFactory.createVirtualFile("test2", "py", false);
        VirtualFile vf3 = TestObjectFactory.createVirtualFile("test3", "py", false);

        vf1.setContent("print(\"Hello world!\"");

        Exercise ex1 = TestObjectFactory.createCodeExercise("Some question");
        ex1.setPublic_files(List.of(vf1, vf2, vf3));

        Assignment assignment = TestObjectFactory.createAssignment("Assignment 1");
        assignment.addExercise(ex1);

        Course course = TestObjectFactory.createCourse("Informatics 1");
        course.setAssignments(List.of(assignment));

        VirtualFile studentFile1 = TestObjectFactory.createVirtualFile("test", "py", false);
        VirtualFile studentFile2 = TestObjectFactory.createVirtualFile("test2", "py", false);
        VirtualFile studentFile3 = TestObjectFactory.createVirtualFile("test3", "py", false);

        studentFile1.setContent("a = 1 + 1\nprint(\"Hello, world!\")");

        CodeAnswer codeAnswer = CodeAnswer.builder()
                .version(1)
                .userId("user-12345")
                .commitId("commit-12345")
                .courseId(course.getId())
                .assignmentId(assignment.getId())
                .exerciseId(ex1.getId())
                .exercise(ex1)
                .timestamp(LocalDateTime.now())
                .isOfficialSubmission(true)
                .publicFiles(List.of(studentFile1, studentFile2, studentFile3))
                .build();

        Assert.assertNotEquals(codeAnswer.getPublicFiles(), ex1.getPublic_files());
        Assert.assertEquals(codeAnswer.getPrivateFiles(), ex1.getPrivate_files());
        Assert.assertEquals(codeAnswer.getResourceFiles(), ex1.getResource_files());
    }

//    @Test
//    public void studentAnswerDTOTextAnswer() {
//        Exercise ex = TestObjectFactory.createCodeExercise("What is 1 + 1?");
//        Assignment assignment = TestObjectFactory.createAssignment("Assignment 1");
//        Course course = TestObjectFactory.createCourse("Informatics 1");
//
//        course.setAssignments(List.of(assignment));
//        assignment.addExercise(ex);
//
//        TextAnswer textAnswer = TextAnswer.builder()
//                .version(1)
//                .userId("user-12345")
//                .commitId("commit-12345")
//                .courseId(course.getId())
//                .assignmentId(assignment.getId())
//                .exerciseId(ex.getId())
//                .exercise(ex)
//                .timestamp(LocalDateTime.now())
//                .answer("11")
//                .build();
//
//        StudentAnswerDTO studentAnswer = StudentAnswerDTO.builder()
//                .type(ex.getType())
//                .
//                .textAnswer("11")
//                .metadata()
//                .build();
//    }
}