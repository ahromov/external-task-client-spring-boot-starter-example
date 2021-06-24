package org.camunda.platform.runtime.example.service.equipement.dto;

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
public class SupplyRateDto extends Dto implements Serializable {

    private Long id;
    private String title;
    private Long equipmentType;
    private Integer amount;
}
