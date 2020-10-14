package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import jp.co.axa.apidemo.request.EmployeeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final String EMPLOYEE_NOT_FOUND = "Employee not found";

    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    public Employee getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(entityNotFoundExceptionSupplier(EMPLOYEE_NOT_FOUND));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Employee saveEmployee(EmployeeRequest request){
        Employee employee = Employee.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .salary(request.getSalary())
                .department(request.getDepartment()).build();
        employeeRepository.save(employee);
        logger.info("Employee saved successfully. EmployeeId: " + employee.getId());
        return employee;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteEmployee(String employeeId){
        //check if exists
        employeeRepository.findById(employeeId).orElseThrow(entityNotFoundExceptionSupplier(EMPLOYEE_NOT_FOUND));
        employeeRepository.deleteById(employeeId);
        logger.info("Employee deleted successfully. EmployeeId: " + employeeId);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Employee updateEmployee(String employeeId, EmployeeRequest request) {
        //check if exists
        employeeRepository.findById(employeeId).orElseThrow(entityNotFoundExceptionSupplier(EMPLOYEE_NOT_FOUND));

        Employee employee = Employee.builder()
                .id(employeeId)
                .name(request.getName())
                .salary(request.getSalary())
                .department(request.getDepartment()).build();
       employeeRepository.save(employee);
       logger.info("Employee updated successfully. EmployeeId: " + employeeId);
       return employee;
    }

    private Supplier<RuntimeException> entityNotFoundExceptionSupplier(String message) {
        return() -> new EntityNotFoundException(message);
    }
}

