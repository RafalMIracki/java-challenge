package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.request.EmployeeRequest;

import java.util.List;

public interface EmployeeService {

    public List<Employee> retrieveEmployees();

    public Employee getEmployee(String employeeId);

    public Employee saveEmployee(EmployeeRequest request);

    public void deleteEmployee(String employeeId);

    public Employee updateEmployee(String employeeId, EmployeeRequest request);
}