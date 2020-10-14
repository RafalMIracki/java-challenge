package jp.co.axa.apidemo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.request.EmployeeRequest;
import jp.co.axa.apidemo.services.EmployeeServiceImpl;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;


@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeControllerIntegrationTest {

    private MockMvc mockMvc;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private EmployeeServiceImpl mockEmployeeService;

    private EmployeeController employeeController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        employeeController = new EmployeeController();
        employeeController.setEmployeeService(mockEmployeeService);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    public void getEmployees_success() throws Exception {
        given(this.mockEmployeeService.retrieveEmployees())
                .willReturn(createEmployees());

        when(mockEmployeeService.retrieveEmployees()).thenReturn(createEmployees());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].salary", is(60000)))
                .andExpect(jsonPath("$[0].department", is("Sales")));
    }

    @Test
    public void getEmployees_empty() throws Exception {
        List<Employee> mockEmployees = new ArrayList<>();
        given(this.mockEmployeeService.retrieveEmployees())
                .willReturn(mockEmployees);

        when(mockEmployeeService.retrieveEmployees()).thenReturn(mockEmployees);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getEmployee_success() throws Exception {
        Employee mockEmployee = createEmployee();
        given(this.mockEmployeeService.getEmployee(mockEmployee.getId()))
                .willReturn(mockEmployee);

        when(mockEmployeeService.getEmployee(mockEmployee.getId())).thenReturn(mockEmployee);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/" + mockEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockEmployee.getId())))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.salary", is(60000)))
                .andExpect(jsonPath("$.department", is("Sales")));
    }

    @Test
    public void getEmployee_notFound() throws Exception {
        Employee mockEmployee = createEmployee();
        given(this.mockEmployeeService.getEmployee(mockEmployee.getId()))
                .willReturn(mockEmployee);

        when(mockEmployeeService.getEmployee(mockEmployee.getId())).thenThrow(new EntityNotFoundException("Employee not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employees/" + mockEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getEmployee_employeeId_empty() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/",""))
                .andExpect(status().isBadRequest());

        assertEquals(HttpMessageNotReadableException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void getEmployee_employeeId_tooLong() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/",
                "1234567890123456789012345678901234567890"))
                .andExpect(status().isBadRequest());

        assertEquals(HttpMessageNotReadableException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void postEmployee_success() throws Exception {
        Employee mockEmployee = createEmployee();

        when(mockEmployeeService.saveEmployee(any(EmployeeRequest.class))).thenReturn(mockEmployee);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8").content(MAPPER.writeValueAsBytes(mockEmployee));

        mockMvc.perform(builder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(mockEmployee.getId())))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.salary", is(60000)))
                .andExpect(jsonPath("$.department", is("Sales")))
                .andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString(mockEmployee)));
    }

    @Test
    public void postEmployee_invalidName() throws Exception {
        Employee mockEmployee = createEmployee();
        mockEmployee.setName("");

        String employeeJsonString = MAPPER.writeValueAsString(mockEmployee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON).content(employeeJsonString)).andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void postEmployee_invalidSalary() throws Exception {
        Employee mockEmployee = createEmployee();
        mockEmployee.setSalary(-5);

        String employeeJsonString = MAPPER.writeValueAsString(mockEmployee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON).content(employeeJsonString)).andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
        resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void postEmployee_invalidDepartment() throws Exception {
        Employee mockEmployee = createEmployee();
        mockEmployee.setDepartment("AB");
        String employeeJsonString = MAPPER.writeValueAsString(mockEmployee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON).content(employeeJsonString)).andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void deleteEmployee_success() throws Exception {
        Employee employee = createEmployee();

        EmployeeServiceImpl serviceSpy = Mockito.spy(mockEmployeeService);
        Mockito.doNothing().when(serviceSpy).deleteEmployee(employee.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/" + employee.getId())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        verify(mockEmployeeService, times(1)).deleteEmployee(employee.getId());
    }

    @Test
    public void deleteEmployee_notFound() throws Exception {
        Employee employee = createEmployee();

        EmployeeServiceImpl serviceSpy = Mockito.spy(mockEmployeeService);
        Mockito.doNothing().when(serviceSpy).deleteEmployee(employee.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/employees/" + employee.getId())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        verify(mockEmployeeService, times(1)).deleteEmployee(employee.getId());
    }

    @Test
    public void putEmployee_success() throws Exception {
        Employee employee = createEmployee();
        EmployeeRequest employeeRequest = createEmployeeRequest(employee);

        when(mockEmployeeService.updateEmployee(employee.getId(), employeeRequest)).thenReturn(employee);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/api/v1/employees/" + employee.getId(), employeeRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
                .content(MAPPER.writeValueAsBytes(employeeRequest));

        mockMvc.perform(builder)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(employee.getId())))
                .andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString(employee)));
    }

    @Test
    public void putEmployee_invalidName() throws Exception {
        Employee employee = createEmployee();
        employee.setName("");
        EmployeeRequest employeeRequest = createEmployeeRequest(employee);

        when(mockEmployeeService.updateEmployee(employee.getId(), employeeRequest)).thenReturn(employee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsBytes(employeeRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void putEmployee_invalidSalary() throws Exception {
        Employee employee = createEmployee();
        employee.setSalary(-1);
        EmployeeRequest employeeRequest = createEmployeeRequest(employee);

        when(mockEmployeeService.updateEmployee(employee.getId(), employeeRequest)).thenReturn(employee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsBytes(employeeRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    @Test
    public void putEmployee_invalidDepartment() throws Exception {
        Employee employee = createEmployee();
        employee.setDepartment("AB");
        EmployeeRequest employeeRequest = createEmployeeRequest(employee);

        when(mockEmployeeService.updateEmployee(employee.getId(), employeeRequest)).thenReturn(employee);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/employees/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsBytes(employeeRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(MethodArgumentNotValidException.class,
                resultActions.andReturn().getResolvedException().getClass());
    }

    private List<Employee> createEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(createEmployee());
        employees.add(createEmployee());
        employees.add(createEmployee());
        return employees;
    }

    private Employee createEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID().toString())
                .name("John")
                .salary(60000)
                .department("Sales")
                .build();
    }

    private EmployeeRequest createEmployeeRequest(Employee employee) {
        return EmployeeRequest.builder()
                .name(employee.getName())
                .salary(employee.getSalary())
                .department(employee.getDepartment())
                .build();
    }
}
