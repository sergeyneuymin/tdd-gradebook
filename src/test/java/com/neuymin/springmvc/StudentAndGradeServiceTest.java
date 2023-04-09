package com.neuymin.springmvc;

import com.neuymin.springmvc.models.*;
import com.neuymin.springmvc.repository.HistoryGradesDao;
import com.neuymin.springmvc.repository.MathGradesDao;
import com.neuymin.springmvc.repository.ScienceGradesDao;
import com.neuymin.springmvc.repository.StudentDao;
import com.neuymin.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setupDatabase() {
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_address) " +
                "values (1, 'Ivan', 'Ivanov', 'email')");

        jdbcTemplate.execute("insert into math_grade(id, student_id, grade) " +
                "values (1, 1, 100.00)");

        jdbcTemplate.execute("insert into science_grade(id, student_id, grade) " +
                "values (1, 1, 100.00)");

        jdbcTemplate.execute("insert into history_grade(id, student_id, grade) " +
                "values (1, 1, 100.00)");
    }

    @Test
    public void createStudentService() {

        studentService.createStudent("Ivan", "Ivanov", "email");

        CollegeStudent student = studentDao.findByEmailAddress("email");

        Assertions.assertEquals("email", student.getEmailAddress(), "find by email");
    }

    @Test
    public void isStudentNullCheck() {
        Assertions.assertTrue(studentService.checkStudentIsNull(1));

        Assertions.assertFalse(studentService.checkStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradesDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradesDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradesDao.findById(1);

        Assertions.assertTrue(deletedCollegeStudent.isPresent(), "Return true");
        Assertions.assertTrue(deletedMathGrade.isPresent(), "Return true");
        Assertions.assertTrue(deletedScienceGrade.isPresent(), "Return true");
        Assertions.assertTrue(deletedHistoryGrade.isPresent(), "Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade = mathGradesDao.findById(1);
        deletedScienceGrade = scienceGradesDao.findById(1);
        deletedHistoryGrade = historyGradesDao.findById(1);

        Assertions.assertFalse(deletedCollegeStudent.isPresent(), "return false");
        Assertions.assertFalse(deletedMathGrade.isPresent(), "return false");
        Assertions.assertFalse(deletedScienceGrade.isPresent(), "return false");
        Assertions.assertFalse(deletedHistoryGrade.isPresent(), "return false");
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradeBook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for(CollegeStudent collegeStudent: iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        Assertions.assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService() {

        // create the grade
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "Math"));
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "Science"));
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "History"));

        //get all grades with students
        Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(1);

        //verify there are grades
        Assertions.assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has math grades");
        Assertions.assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has science grades");
        Assertions.assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has history grades");

    }

    @Test
    public void deleteGradeService() {
        Assertions.assertEquals(1, studentService.deleteGrade(1, "Math"), "Returns student id after delete");
        Assertions.assertEquals(1, studentService.deleteGrade(1, "Science"), "Returns student id after delete");
        Assertions.assertEquals(1, studentService.deleteGrade(1, "History"), "Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        Assertions.assertEquals(0, studentService.deleteGrade(0,"Science"), "No student should have 0 id");
        Assertions.assertEquals(0, studentService.deleteGrade(1,"Literature"), "No student should have 0 id");
    }

    @Test
    public void createGradeServiceReturnFalse() {
        Assertions.assertFalse(studentService.createGrade(105, 1, "Math"));
        Assertions.assertFalse(studentService.createGrade(-5, 1, "Math"));
        Assertions.assertFalse(studentService.createGrade(80.50, 2, "Math"));
        Assertions.assertFalse(studentService.createGrade(80.50, 1, "Literature"));
    }

    @Test
    public void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);
        Assertions.assertNotNull(gradebookCollegeStudent);
        Assertions.assertEquals(1, gradebookCollegeStudent.getId());
        Assertions.assertEquals("Ivan", gradebookCollegeStudent.getFirstname());
        Assertions.assertEquals("Ivanov", gradebookCollegeStudent.getLastname());
        Assertions.assertEquals("mail1", gradebookCollegeStudent.getEmailAddress());
        Assertions.assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        Assertions.assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        Assertions.assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @Test
    public void studentInformationServiceReturnNull() {

        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        Assertions.assertNull(gradebookCollegeStudent);

    }

    @AfterEach
    public void deleteData() {
        jdbcTemplate.execute("DELETE FROM student");
        jdbcTemplate.execute("DELETE FROM math_grade");
        jdbcTemplate.execute("DELETE FROM science_grade");
        jdbcTemplate.execute("DELETE FROM history_grade");

    }


}
