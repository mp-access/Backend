package ch.uzh.ifi.access.course.api;

import ch.uzh.ifi.access.course.CourseApplication;
import ch.uzh.ifi.access.course.Model.Assignment;
import ch.uzh.ifi.access.course.Model.Course;
import ch.uzh.ifi.access.course.Model.Exercise;
import ch.uzh.ifi.access.course.Model.ExerciseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseResource {

    @RequestMapping("/")
    public Course getCourses() {
        return CourseApplication.courseData;
    }

    @RequestMapping("/title")
    public String getCourseTitle() {
        return CourseApplication.courseData.title;
    }

    //courseData currently only consists of one course, so it only checks if that course contains that title. We need to adapt course_structure repo
    @RequestMapping("/byTitle")
    public Course getCourseByTitle(@RequestParam(value = "title") String title) {
        if (CourseApplication.courseData.title.contains(title)) {
            return CourseApplication.courseData;
        } else {
            return null;
        }
    }

    @RequestMapping("/description")
    public String getCourseDescription() {
        return CourseApplication.courseData.description;
    }

    @RequestMapping("/owner")
    public String getCourseOwner() {
        return CourseApplication.courseData.owner;
    }

    @RequestMapping("/startDate")
    public Date getCourseStartDate() {
        return CourseApplication.courseData.startDate;
    }

    @RequestMapping("/endDate")
    public Date getCourseEndDate() {
        return CourseApplication.courseData.endDate;
    }

    @RequestMapping("/assistants")
    public List getCourseAssistants() {
        return CourseApplication.courseData.assistants;
    }

    @RequestMapping("/students")
    public List getCourseStudents() {
        return CourseApplication.courseData.students;
    }

    @RequestMapping("/assignments")
    public List getCourseAssignments() {
        return CourseApplication.courseData.assignments;
    }

    //courseData currently only consists of one assignment. We need to adapt course_structure repo
    @RequestMapping("/assignmentPosition")
    public Assignment getCourseAssignmentPosition(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position);
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/title")
    public String getCourseAssignmentPositionTitle(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position).title;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/description")
    public String getCourseAssignmentPositionDescription(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position).description;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/publishDate")
    public Date getCourseAssignmentPositionPublishDate(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position).publishDate;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/dueDate")
    public Date getCourseAssignmentPositionDueDate(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position).dueDate;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercises")
    public List getCourseAssignmentPositionExercises(@RequestParam(value = "position") int position) {
        if (CourseApplication.courseData.assignments.size() >= position) {
            return CourseApplication.courseData.assignments.get(position).exercises;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition")
    public Exercise getCourseAssignmentPositionExercisePosition(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/type")
    public ExerciseType getCourseAssignmentPositionExercisePositionType(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).type;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/language")
    public String getCourseAssignmentPositionExercisePositionLanguage(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).language;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/question")
    public String getCourseAssignmentPositionExercisePositionQuestion(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).question;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/privateFiles")
    public List getCourseAssignmentPositionExercisePositionPrivateFiles(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).private_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/publicFiles")
    public List getCourseAssignmentPositionExercisePositionPublicFiles(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).public_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/resourceFiles")
    public List getCourseAssignmentPositionExercisePositionResourceFiles(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).resource_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/solutionFiles")
    public List getCourseAssignmentPositionExercisePositionSolutionFiles(@RequestParam(value = "coursePosition") int coursePosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (CourseApplication.courseData.assignments.size() >= coursePosition) {
            if (CourseApplication.courseData.assignments.get(coursePosition).exercises.size() >= exercisePosition) {
                return CourseApplication.courseData.assignments.get(coursePosition).exercises.get(exercisePosition).solution_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /*
    @RequestMapping("/byID")
    public Course getCourseByID(@RequestParam(value="id") String id) {
        return CourseApplication.courseData.getByID;
    }
     */


}