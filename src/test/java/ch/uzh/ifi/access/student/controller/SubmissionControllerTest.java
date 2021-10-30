package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.TestObjectFactory;
import ch.uzh.ifi.access.course.config.CourseAuthentication;
import ch.uzh.ifi.access.course.config.CourseServiceSetup;
import ch.uzh.ifi.access.course.dao.CourseDAO;
import ch.uzh.ifi.access.course.model.Assignment;
import ch.uzh.ifi.access.course.model.Course;
import ch.uzh.ifi.access.course.model.Exercise;
import ch.uzh.ifi.access.student.dao.StudentSubmissionRepository;
import ch.uzh.ifi.access.student.model.CodeSubmission;
import ch.uzh.ifi.access.student.model.MultipleChoiceSubmission;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import ch.uzh.ifi.access.student.model.TextSubmission;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("testing")
@TestPropertySource(properties = {
        "submission.eval.user-rate-limit=false",
})
public class SubmissionControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private StudentSubmissionRepository repository;

    @MockBean
    private CourseDAO courseDAO;

    @MockBean
    private CourseServiceSetup courseServiceSetup;

    private CourseAuthentication studentAuthentication;
    private CourseAuthentication assistantAuthentication;

    private Course course;
    private Exercise exerciseAlreadyPublished;
    private String exerciseIdAlreadyPublished;
    private String exerciseIdNotYetPublished;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        MockitoAnnotations.initMocks(this);

        course = TestObjectFactory.createCourseWithOneAssignmentAndOneExercise("course", "assignment", "question");
        Assignment assignment = course.getAssignments().get(0);
        Exercise exercise = assignment.getExercises().get(0);
        exerciseAlreadyPublished = exercise;
        exerciseIdAlreadyPublished = exercise.getId();
        assignment.setPublishDate(ZonedDateTime.now().minusDays(1));
        assignment.setDueDate(ZonedDateTime.now().plusDays(7));
        when(courseDAO.selectExerciseById(exerciseIdAlreadyPublished)).thenReturn(Optional.of(exercise));

        assignment = TestObjectFactory.createAssignment("asdf");
        exercise = TestObjectFactory.createCodeExercise("adfsf");
        assignment.addExercise(exercise);
        course.addAssignment(assignment);
        assignment.setPublishDate(ZonedDateTime.now().plusDays(1));
        assignment.setDueDate(ZonedDateTime.now().plusDays(7));
        exerciseIdNotYetPublished = exercise.getId();
        when(courseDAO.selectExerciseById(exerciseIdNotYetPublished)).thenReturn(Optional.of(exercise));

        studentAuthentication = TestObjectFactory.createCourseAuthentication(Set.of(TestObjectFactory.createStudentAccess(course.getId())));
        assistantAuthentication = TestObjectFactory.createCourseAuthentication(Set.of(TestObjectFactory.createAdminAccess(course.getId())));
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void submitCode() throws Exception {
        final boolean isGraded = false;
        final String fileName1 = "script";
        final String fileName2 = "testsuite";
        final String extension = "py";

        String payload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName1) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName2) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                String.format("        \"graded\": \"%b\"\n", isGraded) +
                "    }\n" +
                "}";

        SecurityContextHolder.getContext().setAuthentication(studentAuthentication);
        mvc
                .perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                        .with(authentication(studentAuthentication))
                        .with(csrf())
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        StudentSubmission submission = repository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseIdAlreadyPublished, studentAuthentication.getUserId()).orElse(null);
        Assertions.assertThat(submission).isNotNull();
        Assertions.assertThat(submission).isInstanceOf(CodeSubmission.class);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles()).size().isEqualTo(2);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(0).getName()).isEqualTo(fileName1);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(0).getExtension()).isEqualTo(extension);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(1).getName()).isEqualTo(fileName2);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(1).getExtension()).isEqualTo(extension);
    }

    @Test
    public void submitMultipleChoices() throws Exception {
        final String payload = "{\n" +
                "    \"type\": \"multipleChoice\",\n" +
                "    \"details\": {\n" +
                "        \"choices\": [0, 2]\n" +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        StudentSubmission submission = repository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseIdAlreadyPublished, studentAuthentication.getUserId()).orElse(null);
        Assertions.assertThat(submission).isNotNull();
        Assertions.assertThat(submission.getExerciseId()).isEqualTo(exerciseIdAlreadyPublished);
        Assertions.assertThat(submission.getUserId()).isEqualTo(studentAuthentication.getUserId());
        Assertions.assertThat(submission).isInstanceOf(MultipleChoiceSubmission.class);
        Assertions.assertThat(((MultipleChoiceSubmission) submission).getChoices()).containsExactly(0, 2);
    }

    @Test
    public void submitText() throws Exception {
        final String answer = "The answer is 42";
        final String payload = "{\n" +
                "    \"type\": \"text\",\n" +
                "    \"details\": {\n" +
                String.format("        \"answer\": \"%s\"\n", answer) +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        StudentSubmission submission = repository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseIdAlreadyPublished, studentAuthentication.getUserId()).orElse(null);
        Assertions.assertThat(submission).isNotNull();
        Assertions.assertThat(submission.getExerciseId()).isEqualTo(exerciseIdAlreadyPublished);
        Assertions.assertThat(submission.getUserId()).isEqualTo(studentAuthentication.getUserId());
        Assertions.assertThat(submission).isInstanceOf(TextSubmission.class);
        Assertions.assertThat(((TextSubmission) submission).getAnswer()).isEqualTo(answer);
    }

    @Test
    public void submitTextMaxAttemptsReached() throws Exception {
        final String answer = "The answer is 42";
        final String payload = "{\n" +
                "    \"type\": \"text\",\n" +
                "    \"details\": {\n" +
                "    \"graded\": true,\n" +
                String.format("        \"answer\": \"%s\"\n", answer) +
                "    }\n" +
                "}";

        for (int i = 0; i < exerciseAlreadyPublished.getMaxSubmits(); i++) {
            mvc.perform(post("/submissions/exercises/" + exerciseAlreadyPublished.getId())
                    .with(csrf())
                    .with(authentication(studentAuthentication))
                    .contentType("application/json")
                    .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.evalId").exists());
        }

        mvc.perform(post("/submissions/exercises/" + exerciseAlreadyPublished.getId())
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isTooManyRequests());

        int count = repository.countByExerciseIdAndUserIdAndIsInvalidFalseAndIsGradedTrueAndIsTriggeredReSubmissionFalse(exerciseAlreadyPublished.getId(), studentAuthentication.getUserId());
        Assertions.assertThat(count).isEqualTo(exerciseAlreadyPublished.getMaxSubmits());
    }

    @Test
    public void getMostRecentSubmissionForExercise() throws Exception {
        final String payload1 = "{\n" +
                "    \"type\": \"multipleChoice\",\n" +
                "    \"details\": {\n" +
                "        \"choices\": [0, 2]\n" +
                "    }\n" +
                "}";
        final String payload2 = "{\n" +
                "    \"type\": \"multipleChoice\",\n" +
                "    \"details\": {\n" +
                "        \"choices\": [0, 2, 3]\n" +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        mvc.perform(get("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(authentication(studentAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(studentAuthentication.getUserId()))
                .andExpect(jsonPath("$.exerciseId").value(exerciseIdAlreadyPublished))
                .andExpect(jsonPath("$.version").value(0))
                .andExpect(jsonPath("$.choices").isArray())
                .andExpect(jsonPath("$.choices", hasSize(2)))
                .andExpect(jsonPath("$.choices[0]").value(0))
                .andExpect(jsonPath("$.choices[1]").value(2));

        mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        mvc.perform(get("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(authentication(studentAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(studentAuthentication.getUserId()))
                .andExpect(jsonPath("$.exerciseId").value(exerciseIdAlreadyPublished))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.choices").isArray())
                .andExpect(jsonPath("$.choices", hasSize(3)))
                .andExpect(jsonPath("$.choices[0]").value(0))
                .andExpect(jsonPath("$.choices[1]").value(2))
                .andExpect(jsonPath("$.choices[2]").value(3));
    }

    @Test
    public void getSubmissionHistory() throws Exception {
        String payload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                "                \"name\": \"script\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                "                \"name\": \"testsuite\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                "        \"graded\": true\n" +
                "    }\n" +
                "}";

        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                    .with(csrf())
                    .with(authentication(studentAuthentication))
                    .contentType("application/json")
                    .content(payload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.evalId").exists());
        }

        String runPayload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                "                \"name\": \"script\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                "                \"name\": \"testsuite\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                "        \"graded\": false\n" +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdAlreadyPublished)
                .with(csrf())
                .contentType("application/json")
                .content(runPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        mvc.perform(get("/submissions/exercises/" + exerciseIdAlreadyPublished + "/history")
                .with(authentication(studentAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissions").isArray())
                .andExpect(jsonPath("$.submissions", hasSize(3)))
                .andExpect(jsonPath("$.submissions[0].version").value(2))
                .andExpect(jsonPath("$.submissions[1].version").value(1))
                .andExpect(jsonPath("$.submissions[2].version").value(0))
                .andExpect(jsonPath("$.runs").isArray())
                .andExpect(jsonPath("$.runs", hasSize(1)));

    }

    @Test
    public void submitExerciseDoesNotExist() throws Exception {
        String payload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                "                \"name\": \"script\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                "                \"name\": \"testsuite\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                "        \"graded\": \"true\"\n" +
                "    }\n" +
                "}";
        mvc.perform(post("/submissions/exercises/" + UUID.randomUUID().toString())
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isNotFound());
    }


    @Test
    public void submitExerciseNotYetPublished() throws Exception {
        final boolean isGraded = false;
        final String fileName1 = "script";
        final String fileName2 = "testsuite";
        final String extension = "py";

        String payload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName1) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName2) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                String.format("        \"graded\": \"%b\"\n", isGraded) +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdNotYetPublished)
                .with(csrf())
                .with(authentication(studentAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitNotYetPublishedButAssistant() throws Exception {
        final boolean isGraded = false;
        final String fileName1 = "script";
        final String fileName2 = "testsuite";
        final String extension = "py";

        String payload = "{\n" +
                "    \"type\": \"code\",\n" +
                "    \"details\": {\n" +
                "        \"publicFiles\": [\n" +
                "            {\n" +
                "                \"path\": \"/script.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName1) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                String.format("                \"name\": \"%s\",\n", fileName2) +
                String.format("                \"extension\": \"%s\",\n", extension) +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ],\n" +
                String.format("        \"graded\": \"%b\"\n", isGraded) +
                "    }\n" +
                "}";

        mvc.perform(post("/submissions/exercises/" + exerciseIdNotYetPublished + "?admin=true&courseId=" + course.getId())
                .with(csrf())
                .with(authentication(assistantAuthentication))
                .contentType("application/json")
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evalId").exists());

        StudentSubmission submission = repository.findTopByExerciseIdAndUserIdOrderByVersionDesc(exerciseIdNotYetPublished, assistantAuthentication.getUserId()).orElse(null);
        Assertions.assertThat(submission).isNotNull();
        Assertions.assertThat(submission).isInstanceOf(CodeSubmission.class);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles()).size().isEqualTo(2);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(0).getName()).isEqualTo(fileName1);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(0).getExtension()).isEqualTo(extension);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(1).getName()).isEqualTo(fileName2);
        Assertions.assertThat(((CodeSubmission) submission).getPublicFiles().get(1).getExtension()).isEqualTo(extension);
    }
}
