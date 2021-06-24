package org.camunda.platform.runtime.example.service.vacation.dto;

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
public class OrgUnitDto extends Dto implements Serializable {

    private Long id;
    private String title;
    private List<Long> positions;
}
