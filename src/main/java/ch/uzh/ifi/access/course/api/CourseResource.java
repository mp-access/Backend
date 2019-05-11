package ch.uzh.ifi.access.course.api;

import ch.uzh.ifi.access.course.CourseRepository;
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
@RequestMapping("/courses")
public class CourseResource {

    private final CourseRepository courseRepository;

    public CourseResource(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @RequestMapping
    public Course getCourses() {
        return courseRepository.getCourse();
    }

    @RequestMapping("/title")
    public String getCourseTitle() {
        return courseRepository.getCourse().title;
    }

    //courseData currently only consists of one course, so it only checks if that course contains that title. We need to adapt course_structure repo
    @RequestMapping("/byTitle")
    public Course getCourseByTitle(@RequestParam(value = "title") String title) {
        if (courseRepository.getCourse().title.contains(title)) {
            return courseRepository.getCourse();
        } else {
            return null;
        }
    }

    @RequestMapping("/description")
    public String getCourseDescription() {
        return courseRepository.getCourse().description;
    }

    @RequestMapping("/owner")
    public String getCourseOwner() {
        return courseRepository.getCourse().owner;
    }

    @RequestMapping("/startDate")
    public Date getCourseStartDate() {
        return courseRepository.getCourse().startDate;
    }

    @RequestMapping("/endDate")
    public Date getCourseEndDate() {
        return courseRepository.getCourse().endDate;
    }

    @RequestMapping("/assistants")
    public List getCourseAssistants() {
        return courseRepository.getCourse().assistants;
    }

    @RequestMapping("/students")
    public List getCourseStudents() {
        return courseRepository.getCourse().students;
    }

    @RequestMapping("/assignments")
    public List getCourseAssignments() {
        return courseRepository.getCourse().assignments;
    }

    //courseData currently only consists of one assignment. We need to adapt course_structure repo
    @RequestMapping("/assignmentPosition")
    public Assignment getCourseAssignmentPosition(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition);
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/title")
    public String getCourseAssignmentPositionTitle(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition).title;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/description")
    public String getCourseAssignmentPositionDescription(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition).description;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/publishDate")
    public Date getCourseAssignmentPositionPublishDate(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition).publishDate;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/dueDate")
    public Date getCourseAssignmentPositionDueDate(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition).dueDate;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercises")
    public List getCourseAssignmentPositionExercises(@RequestParam(value = "assignmentPosition") int assignmentPosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            return courseRepository.getCourse().assignments.get(assignmentPosition).exercises;
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition")
    public Exercise getCourseAssignmentPositionExercisePosition(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/type")
    public ExerciseType getCourseAssignmentPositionExercisePositionType(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).type;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/language")
    public String getCourseAssignmentPositionExercisePositionLanguage(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).language;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/question")
    public String getCourseAssignmentPositionExercisePositionQuestion(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).question;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/privateFiles")
    public List getCourseAssignmentPositionExercisePositionPrivateFiles(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).private_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/publicFiles")
    public List getCourseAssignmentPositionExercisePositionPublicFiles(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).public_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/resourceFiles")
    public List getCourseAssignmentPositionExercisePositionResourceFiles(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).resource_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping("/assignmentPosition/exercisePosition/solutionFiles")
    public List getCourseAssignmentPositionExercisePositionSolutionFiles(@RequestParam(value = "assignmentPosition") int assignmentPosition, @RequestParam(value = "exercisePosition") int exercisePosition) {
        if (courseRepository.getCourse().assignments.size() >= assignmentPosition) {
            if (courseRepository.getCourse().assignments.get(assignmentPosition).exercises.size() >= exercisePosition) {
                return courseRepository.getCourse().assignments.get(assignmentPosition).exercises.get(exercisePosition).solution_files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}