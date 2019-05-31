package ch.uzh.ifi.access.course.dto;

import ch.uzh.ifi.access.course.model.ExerciseType;
import ch.uzh.ifi.access.course.model.VirtualFile;
import ch.uzh.ifi.access.course.model.workspace.CodeSubmission;
import ch.uzh.ifi.access.course.model.workspace.MultipleChoiceSubmission;
import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import ch.uzh.ifi.access.course.model.workspace.TextSubmission;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class StudentSubmissionDTOTest {

    @Test
    public void createCodeSubmission() throws IOException {
        String json = "{\"publicFiles\": [\n" +
                "            {\n" +
                "                \"id\": \"6\",\n" +
                "                \"path\": \"/script.py\",\n" +
                "                \"name\": \"script\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"if __name__ == '__main__':\\n    p1 = UniPerson(\\\"Hans Muster\\\")\\n    assert p1.__str__() == \\\"Name: Hans Muster\\\"\\n\\n    p1.receive_email(\\\"Email 1\\\")\\n    p1.receive_email(\\\"Email 2\\\")\\n    assert p1.read_emails() == [\\\"Email 1\\\", \\\"Email 2\\\"]\\n    assert p1.read_emails() == []  # Because inbox was emptied after reading the first time\\n\\n    s1 = Student(\\\"Student 1\\\", 2017, False, 40)\\n    assert \\\"Student 1\\\" in s1.__str__()\\n    assert \\\"2017-00000\\\" in s1.__str__()\\n    assert \\\"False\\\" in s1.__str__()\\n    assert \\\"40\\\" in s1.__str__()\\n\\n    s2 = Student(\\\"Student 2\\\", 2017, True, 120)\\n    assert \\\"Student 2\\\" in s2.__str__()\\n    assert \\\"2017-00001\\\" in s2.__str__()\\n    assert \\\"True\\\" in s2.__str__()\\n    assert \\\"120\\\" in s2.__str__()\\n\\n    s3 = Student(\\\"Student 3\\\", 2016, True, 180)\\n    assert \\\"Student 3\\\" in s3.__str__()\\n    assert \\\"2016-00000\\\" in s3.__str__()\\n    assert \\\"True\\\" in s3.__str__()\\n    assert \\\"180\\\" in s3.__str__()\\n\\n    mgmt = UniManagement()\\n\\n    lecturer = Lecturer(\\\"Prof. Dr. Harald Gall\\\", \\\"Informatik 1\\\")\\n\\n    mgmt.add_person(s1)\\n    mgmt.add_person(s2)\\n    mgmt.add_person(s3)\\n    mgmt.add_person(lecturer)\\n\\n    assert mgmt.count_alumni() == 2\\n\\n    mgmt.send_email(\\\"This test email is sent to all university persons.\\\")\\n    assert s1.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s2.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert s3.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n    assert lecturer.read_emails() == [\\\"This test email is sent to all university persons.\\\"]\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"7\",\n" +
                "                \"path\": \"/testsuite.py\",\n" +
                "                \"name\": \"testsuite\",\n" +
                "                \"extension\": \"py\",\n" +
                "                \"content\": \"from unittest import TestCase\\n\\nfrom task_2 import UniPerson, Student, Lecturer, UniManagement\\n\\n\\nclass Task2Test(TestCase):\\n\\n    def setUp(self):\\n        self.person1 = UniPerson(\\\"Person1\\\")\\n\\n        self.student1 = Student(\\\"Student1\\\", 2000, True, 120)\\n        self.student2 = Student(\\\"Student2\\\", 2000, False, 119)\\n        self.student3 = Student(\\\"Student3\\\", 2001, True, 180)\\n\\n        self.lecturer1 = Lecturer(\\\"Lecturer1\\\", \\\"Info1\\\")\\n\\n        self.mgmt = UniManagement()\\n\\n    def test_uni_person_init(self):\\n        self.assertTrue(hasattr(self.person1, \\\"_name\\\"), \\\"You must initialize the _name variable for UniPerson\\\")\\n        self.assertEqual(self.person1._name, \\\"Person1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.person1, \\\"_UniPerson__inbox\\\"), \\\"You must initialize __inbox as an empty list\\\")\\n        self.assertEqual(self.person1._UniPerson__inbox, [], \\\"__inbox seems wrong\\\")\\n\\n    def test_uni_person_str(self):\\n        self.assertEqual(self.person1.__str__(), \\\"Name: Person1\\\", \\\"__str__ of UniPerson seems wrong\\\")\\n\\n    def test_uni_person_email(self):\\n        self.assertTrue(hasattr(self.person1, \\\"read_emails\\\"), \\\"You must declare read_emails\\\")\\n        self.assertTrue(hasattr(self.person1, \\\"receive_email\\\"), \\\"You must declare receive_email\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails before receiving any emails should return an empty inbox\\\")\\n\\n        self.person1.receive_email(\\\"Email1\\\")\\n        self.person1.receive_email(\\\"Email2\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\", \\\"Email2\\\"], \\\"Reading emails seems wrong\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [], \\\"Reading emails should clear inbox\\\")\\n\\n    def test_student_init(self):\\n        self.assertTrue(isinstance(self.student1, UniPerson), \\\"Student must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"_name\\\"), \\\"You must initialize the _name variable for Student\\\")\\n        self.assertEqual(self.student1._name, \\\"Student1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize has_graduated for Student\\\")\\n        self.assertTrue(self.student1.has_graduated, \\\"has_graduated seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.student1, \\\"has_graduated\\\"), \\\"You must initialize __ects for Student\\\")\\n        self.assertEqual(self.student1._Student__ects, 120, \\\"__ects seems wrong\\\")\\n\\n    def test_student_legi_nr(self):\\n        # Create new students to avoid side effects\\n        s1 = Student(\\\"S1\\\", 2015, False, 0)\\n\\n        for i in range(1500):\\n            s = Student(\\\"\\\", 2015, False, 0)\\n\\n        s1502 = Student(\\\"S1502\\\", 2015, False, 0)\\n\\n        s3 = Student(\\\"S3\\\", 2016, False, 0)\\n\\n        self.assertTrue(hasattr(s1, \\\"_Student__legi_nr\\\"), \\\"You must initialize __legi_nr for Student\\\")\\n\\n        self.assertEqual(s1._Student__legi_nr, \\\"2015-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s1502._Student__legi_nr, \\\"2015-01501\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n        self.assertEqual(s3._Student__legi_nr, \\\"2016-00000\\\", \\\"Your implementation of legi_nr seems wrong\\\")\\n\\n    def test_student_str(self):\\n        self.assertTrue(\\\"True\\\" in self.student1.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n        self.assertTrue(\\\"False\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of has_graduated\\\")\\n\\n        self.assertTrue(\\\"119\\\" in self.student2.__str__(), \\\"Your __str__ implementation of Student must contain the value of __ects\\\")\\n\\n        s1 = Student(\\\"S1\\\", 2018, False, 0)\\n        self.assertTrue(\\\"2018-00000\\\" in s1.__str__(), \\\"Your __str_ implementation of Student must contain the value of __legi_nr\\\")\\n\\n    def test_lecturer_init(self):\\n        self.assertTrue(isinstance(self.lecturer1, UniPerson), \\\"Lecturer must inherit from UniPerson\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_name\\\"), \\\"You must initialize the _name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._name, \\\"Lecturer1\\\", \\\"_name seems wrong\\\")\\n\\n        self.assertTrue(hasattr(self.lecturer1, \\\"_Lecturer__lecture_name\\\"), \\\"You must initialize the __lecture_name variable for Lecturer\\\")\\n        self.assertEqual(self.lecturer1._Lecturer__lecture_name, \\\"Info1\\\", \\\"__lecture_name seems wrong\\\")\\n\\n    def test_lecturer_str(self):\\n        self.assertTrue(\\\"Info1\\\" in self.lecturer1.__str__(), \\\"Your __str__ implementation of Lecturer must contain the value of __lecture_name\\\")\\n\\n    def test_mgmt_init(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"_UniManagement__persons\\\"), \\\"You must initialize __persons for UniManagement\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [], \\\"__persons seems wrong\\\")\\n\\n    def test_mgmt_add_person(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"add_person\\\"), \\\"You must implement the add_person method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.student1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(self.lecturer1)\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong\\\")\\n\\n        self.mgmt.add_person(\\\"Wrong data type\\\")\\n        self.assertEqual(self.mgmt._UniManagement__persons, [self.student1, self.lecturer1], \\\"Your implementation of add_person seems wrong; it shouldn't be possible to add a wrong data type\\\")\\n\\n    def test_mgmt_list_persons(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"list_persons\\\"), \\\"You must implement the list_persons method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.list_persons(), [], \\\"list_persons seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        persons = self.mgmt.list_persons()\\n\\n        self.assertTrue(isinstance(persons, list), \\\"list_persons must return a list\\\")\\n        self.assertEqual(len(persons), 3, \\\"The length of your list seems wrong\\\")\\n\\n        self.assertEqual(persons[0], self.person1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[1], self.student1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n        self.assertEqual(persons[2], self.lecturer1.__str__(), \\\"The elements of list_persons should be the __str__ representations of the persons\\\")\\n\\n    def test_mgmt_send_email(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"send_email\\\"), \\\"You must implement the send_email method for UniManagement\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.mgmt.send_email(\\\"Email1\\\")\\n\\n        self.assertEqual(self.person1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.student1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n        self.assertEqual(self.lecturer1.read_emails(), [\\\"Email1\\\"], \\\"send_email seems wrong\\\")\\n\\n    def test_mgmt_count_alumni(self):\\n        self.assertTrue(hasattr(self.mgmt, \\\"count_alumni\\\"), \\\"You must implement the count_alumni method for UniManagement\\\")\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 0, \\\"count_alumni seems wrong\\\")\\n\\n        self.mgmt.add_person(self.person1)\\n        self.mgmt.add_person(self.student1)\\n        self.mgmt.add_person(self.student2)\\n        self.mgmt.add_person(self.lecturer1)\\n\\n        self.assertEqual(self.mgmt.count_alumni(), 1, \\\"count_alumni seems wrong\\\")\\n        self.mgmt.add_person(self.student3)\\n        self.assertEqual(self.mgmt.count_alumni(), 2, \\\"count_alumni seems wrong\\\")\\n\",\n" +
                "                \"isMediaType\": false\n" +
                "            }\n" +
                "        ]}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode detailsJson = mapper.readTree(json);

        StudentAnswerDTO dto = StudentAnswerDTO.builder()
                .type(ExerciseType.code)
                .details(detailsJson)
                .build();

        StudentSubmission studentSubmission = dto.createSubmission();
        Assert.assertEquals(studentSubmission.getClass(), CodeSubmission.class);

        CodeSubmission codeSubmission = (CodeSubmission) studentSubmission;
        Assert.assertEquals(codeSubmission.getPublicFiles().size(), 2);

        VirtualFile vf1 = codeSubmission.getPublicFiles().get(0);
        VirtualFile vf2 = codeSubmission.getPublicFiles().get(1);
        Assert.assertEquals(vf1.getId(), "6");
        Assert.assertEquals(vf2.getId(), "7");

        Assert.assertEquals(vf1.getName(), "script");
        Assert.assertEquals(vf1.getExtension(), "py");

        Assert.assertEquals(vf2.getName(), "testsuite");
        Assert.assertEquals(vf2.getExtension(), "py");
    }

    @Test
    public void createTextSubmission() throws IOException {
        String json = "{\n" +
                "        \"answer\": \"11\"\n" +
                "    }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode detailsJson = mapper.readTree(json);

        StudentAnswerDTO dto = StudentAnswerDTO.builder()
                .type(ExerciseType.text)
                .details(detailsJson)
                .build();

        StudentSubmission studentSubmission = dto.createSubmission();
        Assert.assertEquals(studentSubmission.getClass(), TextSubmission.class);

        TextSubmission textAnswer = (TextSubmission) studentSubmission;
        Assert.assertEquals(textAnswer.getAnswer(), "11");
    }

    @Test
    public void createMultipleChoiceSubmission() throws IOException {
        String json = "{\n" +
                "        \"choices\": [0, 2]\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode detailsJson = mapper.readTree(json);

        StudentAnswerDTO dto = StudentAnswerDTO.builder()
                .type(ExerciseType.multipleChoice)
                .details(detailsJson)
                .build();

        StudentSubmission studentSubmission = dto.createSubmission();
        Assert.assertEquals(studentSubmission.getClass(), MultipleChoiceSubmission.class);

        MultipleChoiceSubmission multipleChoiceSubmission = (MultipleChoiceSubmission) studentSubmission;
        Assert.assertEquals(multipleChoiceSubmission.getChoices(), Set.of(0, 2));
    }
}