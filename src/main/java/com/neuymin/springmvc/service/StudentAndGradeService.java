package com.neuymin.springmvc.service;

import com.neuymin.springmvc.models.CollegeStudent;
import com.neuymin.springmvc.models.HistoryGrade;
import com.neuymin.springmvc.models.MathGrade;
import com.neuymin.springmvc.models.ScienceGrade;
import com.neuymin.springmvc.repository.HistoryGradesDao;
import com.neuymin.springmvc.repository.MathGradesDao;
import com.neuymin.springmvc.repository.ScienceGradesDao;
import com.neuymin.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGrade scienceGrade;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGrade historyGrade;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    public void createStudent(String firstname, String lastname, String email) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, email);
        student.setId(1);
        studentDao.save(student);
    }

    public boolean checkStudentIsNull(int i) {
        Optional<CollegeStudent> student = studentDao.findById(i);
        if(student.isPresent()) return true;
        return false;
    }

    public void deleteStudent(int i) {
        if(checkStudentIsNull(i))
        studentDao.deleteById(i);
    }

    public Iterable<CollegeStudent> getGradeBook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {

        if(!checkStudentIsNull(studentId)) return false;

        if(grade >= 0 && grade <= 100) {
            if(gradeType.equals("Math")) {
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);

                mathGradesDao.save(mathGrade);
                return true;
            }

            if(gradeType.equals("Science")) {
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);

                scienceGradesDao.save(scienceGrade);
                return true;
            }

            if(gradeType.equals("History")) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);

                historyGradesDao.save(historyGrade);
                return true;
            }
        }

        return false;
    }
}
