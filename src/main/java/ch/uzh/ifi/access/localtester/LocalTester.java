package ch.uzh.ifi.access.localtester;

import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.util.RepoCacher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalTester {
    public static void main(String[] args){
        List<File> repos = new ArrayList<>();
        for(String s : args){
            File folder = new File(s);
            if(folder.exists() && folder.isDirectory()){
                repos.add(folder);
            }else{
                System.out.println("ERROR: The folder \"" + folder.getName() + "\" was not found!");
            }
        }
        List<Course> courses = RepoCacher.retrieveCourseData(repos);

        System.out.println("----------------- STARTING TEST ------------------");
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
        System.out.println("----------------- ENDING TEST ------------------");
    }
}
