package org.camunda.platform.runtime.example.service.equipement;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.camunda.platform.runtime.example.service.equipement.dto.SupplyRateDto;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OfficeEquipmentsDistributionService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${erp.base.url}")
    private String baseUrl;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = Long.valueOf((String) delegateExecution.getVariable("positionId"));
        PositionDto positionDto = this.restTemplate.getForObject(baseUrl + "/api/rest/org-structure/position/" + positionId, PositionDto.class);
        List<SupplyRateDto> supplyRatesDtos = Arrays.asList(restTemplate.getForEntity(baseUrl + "/api/rest/equipment/supply-rate", SupplyRateDto[].class).getBody());
        List<EquipmentDto> equipementsDtos = Arrays.asList(restTemplate.getForEntity(baseUrl + "/api/rest/equipment/supply-rate", EquipmentDto[].class).getBody());

        for (long rateId : positionDto.getSupplyRates()) {
            supplyRatesDtos.stream().filter(supplyRateDto -> supplyRateDto.getId() == rateId).forEach(supplyRateDto -> {
                List<EquipmentDto> allEquipmentDtosByType = collectAllEquipesByType(equipementsDtos, supplyRateDto);
                if (!allEquipmentDtosByType.isEmpty()) {
                    List<String> equipIds = new ArrayList<>();
                    allEquipmentDtosByType.forEach(equipmentDto -> {
                        if (equipmentDto.getUser() == null && (equipmentDto.getDecommissioningDate() == null || LocalDate.now().isBefore(equipmentDto.getDecommissioningDate()))) {
                            equipIds.add(equipmentDto.getSerialNumber());
                        }
                        delegateExecution.setVariable("equipIds", equipIds);
                    });
                } else {
                    delegateExecution.setVariable("isDistributed", false);
                    return;
                }
            });
        }

        delegateExecution.setVariable("isDistributed", true);
    }

    private List<EquipmentDto> collectAllEquipesByType(List<EquipmentDto> equipementsDtos, SupplyRateDto supplyRateDto) {
        List<EquipmentDto> allEquipmentDtosByType = new ArrayList<>();
        equipementsDtos.forEach(equipmentDto -> {
            if (Long.valueOf(equipmentDto.getType()) == Long.valueOf(supplyRateDto.getEquipmentType())) {
                allEquipmentDtosByType.add(equipmentDto);
            }
        });
        return allEquipmentDtosByType;
    }
}
