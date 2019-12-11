package ch.uzh.ifi.access.localtester;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.util.RepoCacher;

import java.io.File;
import java.util.List;

public class LocalTester {
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("ERROR: Too few arguments. You gave " + args.length + ". Please provide path to local repository!");
            return;
        }

        boolean showDetails = args.length >= 2 && args[1].equals("-d");


        File dir = new File(args[0]);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("ERROR: The folder \"" + dir.getName() + "\" was not found!");
            return;
        }

        Course c = RepoCacher.retrieveLocalCourseData(List.of(dir)).get(0);


        if (showDetails) {
            System.out.println("----------------- START PARSED FIELDS ------------------");

            String s = c.toString()
                    //.replaceAll(", ", "\n")
                    .replaceAll("super=.*?\\(", "")
                    .replaceAll("([^,]+?)=([^=]+), ", "$1: $2\n")
                    .replaceAll("Course\\(", "Course:\n")
                    .replaceAll("Assignment\\(", "\nAssignment:\n")
                    .replaceAll("Exercise\\(", "\nExercise:\n");

            System.out.println(s);

            System.out.println("----------------- END PARSED FIELDS ------------------\n\n");
        }

        System.out.println("----------------- START PARSING TEST ------------------");

        int err = 0;
        int ascnt = 0;
        int excnt = 0;


        err += testCourse(c);
        ascnt += c.getAssignments().size();

        for (Assignment a : c.getAssignments()) {
            err += testAssignment(a);
            excnt += a.getExercises().size();
            for (Exercise e : a.getExercises()) {
                err += testExercise(e);
            }
        }


        System.out.println("------------------------------------------------------");
        System.out.println("Found " + ascnt + " total Assignments");
        System.out.println("Found " + excnt + " total Exercises");
        System.out.println("------------------------------------------------------");
        System.out.println(("Test ended with " + err + " errors"));
        if (err == 0) {
            System.out.println("Everything seems to be fine :)");
        } else {
            System.out.println("You have some errors to fix :(");
        }

        System.out.println("----------------- END PARSING TEST ------------------\n");
    }

    private static int testCourse(Course c) {
        if (c.getTitle() == null) {
            System.out.println("ERROR: Course 'title' must not be empty!");
            return 1;
        }

        if (c.getStartDate() == null) {
            System.out.println("ERROR: Course 'startDate' must not be empty! (" + c.getTitle() + ")");
            return 1;
        }

        if (c.getEndDate() == null) {
            System.out.println("ERROR: Course 'endDate' must not be empty! (" + c.getTitle() + ")");
            return 1;
        }

        return 0;
    }

    private static int testAssignment(Assignment a) {

        if (a.getTitle() == null) {
            System.out.println("ERROR: Assignment 'title' must not be empty! (Assignment " + a.getOrder() + " - " + a.getTitle() + ")");
            return 1;
        }

        if (a.getPublishDate() == null) {
            System.out.println("ERROR: Assignment 'publishDate' must not be empty! (Assignment " + a.getOrder() + " - " + a.getTitle() + ")");
            return 1;
        }

        if (a.getDueDate() == null) {
            System.out.println("ERROR: Assignment 'dueDate' must not be empty! (Assignment " + a.getOrder() + " - " + a.getTitle() + ")");
            return 1;
        }

        return 0;
    }

    private static int testExercise(Exercise e) {
        Assignment a = e.getAssignment();

        if (e.getType() == null) {
            System.out.println("ERROR: Exercise 'type' must not be empty! (Assignment " + a.getOrder() + " / Exercise " + e.getOrder() + ")");
            return 1;
        } else {
            if ((e.getType() == ExerciseType.code || e.getType() == ExerciseType.codeSnippet) &&
                    e.getLanguage() == null) {
                System.out.println("ERROR: Exercise of type 'code' and 'codeSnippet' needs to provide a 'language'! (Assignment " + a.getOrder() + " / Exercise " + e.getOrder() + ")");
                return 1;
            }

            if ((e.getType() == ExerciseType.multipleChoice || e.getType() == ExerciseType.singleChoice) &&
                    e.getOptions() == null) {
                System.out.println("ERROR: Exercise of type 'singleChoice' and 'multipleChoice' must provide ¨options¨! (Assignment " + a.getOrder() + " / Exercise " + e.getOrder() + ")");
                return 1;
            }

            if ((e.getType() == ExerciseType.multipleChoice || e.getType() == ExerciseType.singleChoice || e.getType() == ExerciseType.text) &&
                    e.getSolutions() == null) {
                System.out.println("ERROR: Exercise of type 'singleChoice', 'multipleChoice' and 'text' must provide 'solutions'! (Assignment " + a.getOrder() + " / Exercise " + e.getOrder() + ")");
                return 1;
            }
        }

        return 0;
    }
}
