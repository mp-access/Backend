package ch.uzh.ifi.access.localtester;

import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.util.RepoCacher;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocalTester {
    public static void main(String[] args){
        if(args.length <= 0){
            System.out.println("ERROR: To few arguments. You gave " + args.length + ". Please provide path to local repository!");
            return;
        }

        List<File> repos = new ArrayList<>();
        for(String s : args){
            File folder = new File(s);
            if(folder.exists() && folder.isDirectory()){
                repos.add(folder);
            }else{
                System.out.println("ERROR: The folder \"" + folder.getName() + "\" was not found!");
                return;
            }
        }
        List<Course> courses = RepoCacher.retrieveCourseData(repos);

        System.out.println("----------------- START PARSING TEST ------------------");

        for(Course c : courses){
            testCourse(c);
            for(Assignment a : c.getAssignments()){
                testAssignment(a);
                for(Exercise e : a.getExercises()){
                    testExercise(e);
                }
            }
        }

        System.out.println("----------------- END PARSING TEST ------------------\n");


        System.out.println("----------------- START PARSED FIELDS ------------------");
        for(Course c : courses) {
            String s = c.toString()
                    //.replaceAll(", ", "\n")
                    .replaceAll("super=.*?\\(", "")
                    .replaceAll("([^,]+?)=([^=]+), ", "$1: $2\n")
                    .replaceAll("Course\\(", "Course:\n")
                    .replaceAll("Assignment\\(", "\nAssignment:\n")
                    .replaceAll("Exercise\\(", "\nExercise:\n");

            System.out.println(s);
        }
        System.out.println("----------------- START PARSED FIELDS ------------------");
    }

    public static void testCourse(Course c){
        if(c.getTitle() == null)
            System.out.println("ERROR: Course 'title' must not be empty!");

        if(c.getStartDate() == null)
            System.out.println("ERROR: Course 'startDate' must not be empty! (" + c.getTitle() + ")");

        if(c.getEndDate() == null)
            System.out.println("ERROR: Course 'endDate' must not be empty! (" + c.getTitle() + ")");
    }

    public static void testAssignment(Assignment a){
        if(a.getTitle() == null)
            System.out.println("ERROR: Assignment 'title' must not be empty! (" + a.getTitle() + ")");

        if(a.getPublishDate() == null)
            System.out.println("ERROR: Assignment 'publishDate' must not be empty! (" + a.getTitle() + ")");

        if(a.getDueDate()== null)
            System.out.println("ERROR: Assignment 'dueDate' must not be empty! (" + a.getTitle() + ")");
    }

    public static void testExercise(Exercise e){
        if(e.getType() == null) {
            System.out.println("ERROR: Exercise 'type' must not be empty! (Exercise " + e.getIndex() + ")");

        }else{
            if ((e.getType() == ExerciseType.code || e.getType() == ExerciseType.codeSnippet) &&
            e.getLanguage() == null)
                System.out.println("ERROR: Exercise of type 'code' and 'codeSnippet' needs to provide a 'language'! (Exercise " + e.getIndex() + ")");

            if ((e.getType() == ExerciseType.multipleChoice || e.getType() == ExerciseType.singleChoice) &&
                    e.getOptions() == null)
                System.out.println("ERROR: Exercise of type 'singleChoice' and 'multipleChoice' must provide ¨options¨! (Exercise " + e.getIndex() + ")");

            if ((e.getType() == ExerciseType.multipleChoice || e.getType() == ExerciseType.singleChoice || e.getType() == ExerciseType.text) &&
                    e.getSolutions() == null)
                System.out.println("ERROR: Exercise of type 'singleChoice', 'multipleChoice' and 'text' must provide 'solutions'! (Exercise " + e.getIndex() + ")");
        }
    }
}
