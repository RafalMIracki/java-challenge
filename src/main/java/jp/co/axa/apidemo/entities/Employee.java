package jp.co.axa.apidemo.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Id
    private String id;

    @Size(min = 3, max = 20)
    @Column(name="EMPLOYEE_NAME")
    private String name;

    @Min(0)
    @Max(Integer.MAX_VALUE)
    @Column(name="EMPLOYEE_SALARY")
    private Integer salary;

    @Size(min = 3, max = 20)
    @Column(name="DEPARTMENT")
    private String department;
}
