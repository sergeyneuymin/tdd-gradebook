package com.neuymin.springmvc;

import com.neuymin.springmvc.models.CollegeStudent;
import com.neuymin.springmvc.models.GradebookCollegeStudent;
import com.neuymin.springmvc.repository.StudentDao;
import com.neuymin.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.addParameter("firstname", "Sergey");
        request.addParameter("lastname", "Neuymin");
        request.addParameter("emailAddress", "sneuymin@mail.ru");
    }

    @BeforeEach
    public void beforeEach() {
        jdbc.execute("insert into student(id, firstname, lastname, email_address) " +
                "values (1, 'Elena', 'Ivanova', 'mail')");
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent student1 = new GradebookCollegeStudent("Ivan", "Ivanov", "mail1");
        CollegeStudent student2 = new GradebookCollegeStudent("Elena", "Ivanova", "mail2");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(student1, student2));

        when(studentCreateServiceMock.getGradeBook()).thenReturn(collegeStudentList);

        Assertions.assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception {

        CollegeStudent student1 = new CollegeStudent("Ivan", "Ivanov", "ivanov@mail.ru");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(student1));

        when(studentCreateServiceMock.getGradeBook()).thenReturn(students);

        Assertions.assertIterableEquals(students, studentCreateServiceMock.getGradeBook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");


        CollegeStudent verifyStudent = studentDao.findByEmailAddress("sneuymin@mail.ru");

        Assertions.assertNotNull(verifyStudent, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        Assertions.assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav  = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        Assertions.assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(
                "/delete/student/{id}", 0
        )).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @AfterEach
    public void deleteData() {
        jdbc.execute("DELETE FROM student");
    }
}
