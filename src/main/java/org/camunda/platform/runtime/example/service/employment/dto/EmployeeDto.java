package org.camunda.platform.runtime.example.service.employment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate fireDate;
    private Long position;
}
