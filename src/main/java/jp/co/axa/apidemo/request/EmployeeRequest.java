package jp.co.axa.apidemo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;

    @NotNull
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private Integer salary;

    @NotBlank
    @Size(min = 3, max = 20)
    private String department;
}
