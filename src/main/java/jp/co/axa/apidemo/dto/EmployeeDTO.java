package jp.co.axa.apidemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EmployeeDTO {
    private String id;
    private String name;
    private Integer salary;
    private String department;
}
