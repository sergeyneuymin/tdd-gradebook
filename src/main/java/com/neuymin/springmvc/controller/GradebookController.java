package com.neuymin.springmvc.controller;

import com.neuymin.springmvc.models.*;
import com.neuymin.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
        studentAndGradeService.createStudent(student.getFirstname(), student.getLastname(),
                student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
        m.addAttribute("students", collegeStudents);

        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudentId(@PathVariable int id, Model m) {

        if(!studentAndGradeService.checkStudentIsNull(id)) return "error";

        studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        return "studentInformation";
    }

}
