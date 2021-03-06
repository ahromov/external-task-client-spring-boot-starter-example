package org.camunda.platform.runtime.example.service.position.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.platform.runtime.example.service.rest.dto.Dto;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDto extends Dto implements Serializable {

    private Long id;
    private String title;
    private Long orgUnit;
    private Long employee;
    private Integer index;
    private List<Long> supplyRates;
}
