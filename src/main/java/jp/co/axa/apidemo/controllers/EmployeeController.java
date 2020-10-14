package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.controllers.error.ErrorResponse;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.request.EmployeeRequest;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity getEmployees() {
        List<Employee> employees = employeeService.retrieveEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity getEmployee(@Valid @PathVariable(name="employeeId") @NotBlank @Size(max = 36) String employeeId) {
        Employee employee = employeeService.getEmployee(employeeId);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @PostMapping("/employees")
    public ResponseEntity saveEmployee(@RequestBody @Valid EmployeeRequest request){
        Employee employee = employeeService.saveEmployee(request);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity deleteEmployee(@PathVariable(name="employeeId") @NotBlank @Size(max = 36) String employeeId){
        employeeService.deleteEmployee(employeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity updateEmployee(
            @PathVariable(name="employeeId") @NotBlank @Size(max = 36) String employeeId,
            @RequestBody @Valid EmployeeRequest employee) {
        Employee emp = employeeService.updateEmployee(employeeId, employee);
        return new ResponseEntity<>(emp, HttpStatus.ACCEPTED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "404", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "400", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String errorCode, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
