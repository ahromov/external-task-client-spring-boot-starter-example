package org.camunda.platform.runtime.example.service.position.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDto {

    private Long id;
    private String title;
    private Long orgUnit;
    private List<Long> supplyRates;
}
