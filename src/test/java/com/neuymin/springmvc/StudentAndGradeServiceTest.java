package com.neuymin.springmvc;

import com.neuymin.springmvc.models.CollegeStudent;
import com.neuymin.springmvc.models.HistoryGrade;
import com.neuymin.springmvc.models.MathGrade;
import com.neuymin.springmvc.models.ScienceGrade;
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

        Assertions.assertTrue(deletedCollegeStudent.isPresent(), "Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);

        Assertions.assertFalse(deletedCollegeStudent.isPresent(), "return false");
    }

    @AfterEach
    public void deleteData() {
        jdbcTemplate.execute("DELETE FROM student");
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
        Assertions.assertTrue(mathGrades.iterator().hasNext(), "Student has math grades");
        Assertions.assertTrue(scienceGrades.iterator().hasNext(), "Student has science grades");
        Assertions.assertTrue(historyGrades.iterator().hasNext(), "Student has history grades");

    }


}
