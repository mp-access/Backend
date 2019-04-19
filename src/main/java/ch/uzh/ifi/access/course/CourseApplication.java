package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Course;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
public class CourseApplication {
    public static Course CourseData;

    public static void main(String[] args) {

//        try {
//            CourseData = RepoCacher.retrieveCourseData();
//
            SpringApplication.run(CourseApplication.class, args);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

}
