package ch.uzh.ifi.access.course;

import ch.uzh.ifi.access.course.Model.Course;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CourseApplication {
	public 	static Course CourseData;

	public static void main(String[] args) {

		try {
			CourseData = RepoCacher.retrieveCourseData();

			SpringApplication.run(CourseApplication.class, args);
		}catch(Exception e)
		{
			e.printStackTrace();
		}


	}

}
