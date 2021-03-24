package org.camunda.platform.runtime.example.service.equipement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentTypeDto {

    private Long id;
    private String title;
    private String description;
    private Integer lifeTimeInMonth;
    private Set<Long> maintenanceSet;
    private Set<Long> consumptionRates;
}
