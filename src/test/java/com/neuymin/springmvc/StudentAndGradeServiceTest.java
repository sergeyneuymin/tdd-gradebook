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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource("/application-test.properties")
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
    JdbcTemplate jdbc;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {

        jdbc.execute(sqlAddStudent);

        jdbc.execute(sqlAddMathGrade);

        jdbc.execute(sqlAddScienceGrade);

        jdbc.execute(sqlAddHistoryGrade);

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

        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService() {

        // create the grade
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "math"));
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "science"));
        Assertions.assertTrue(studentService.createGrade(80.50, 1, "history"));

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
        Assertions.assertEquals(1, studentService.deleteGrade(1, "math"), "Returns student id after delete");
        Assertions.assertEquals(1, studentService.deleteGrade(1, "science"), "Returns student id after delete");
        Assertions.assertEquals(1, studentService.deleteGrade(1, "history"), "Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        Assertions.assertEquals(0, studentService.deleteGrade(0, "science"), "No student should have 0 id");
        Assertions.assertEquals(0, studentService.deleteGrade(1, "literature"), "No student should have 0 id");
    }

    @Test
    public void createGradeServiceReturnFalse() {
        Assertions.assertFalse(studentService.createGrade(105, 1, "math"));
        Assertions.assertFalse(studentService.createGrade(-5, 1, "math"));
        Assertions.assertFalse(studentService.createGrade(80.50, 2, "math"));
        Assertions.assertFalse(studentService.createGrade(80.50, 1, "literature"));
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
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);

    }


}
