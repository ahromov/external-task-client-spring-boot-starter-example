package org.camunda.platform.runtime.example.service.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.platform.runtime.example.service.rest.dto.Dto;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationDto extends Dto implements Serializable {

    private Long employee;
}
