package org.camunda.platform.runtime.example.service.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgUnitDto {

    private Long id;
    private String title;
    private List<Long> positions;
}
