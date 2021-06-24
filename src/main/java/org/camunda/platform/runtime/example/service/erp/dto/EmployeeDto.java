package org.camunda.platform.runtime.example.service.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.platform.runtime.example.service.rest.dto.Dto;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto extends Dto implements Serializable {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate fireDate;
    private Long position;
}
