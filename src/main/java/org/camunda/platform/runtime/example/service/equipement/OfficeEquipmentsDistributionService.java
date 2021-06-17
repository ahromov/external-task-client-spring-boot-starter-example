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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfficeEquipmentsDistributionService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${erp.base.url}")
    private String baseUrl;

    private Set<EquipmentDto> notExistedEqipes = new LinkedHashSet<>();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable("positionId");
        PositionDto positionDto = this.restTemplate.getForObject(baseUrl + "/api/rest/org-structure/position/" + positionId, PositionDto.class);
        List<EquipmentDto> equipementsDtos = Arrays.asList(restTemplate.getForEntity(baseUrl + "/api/rest/equipment", EquipmentDto[].class).getBody());

        final int[] equipesAmount = {0};
        List<SupplyRateDto> supplyRatesDtos = positionDto.getSupplyRates().stream().map(aLong -> {
            SupplyRateDto forObject = this.restTemplate.getForObject(baseUrl + "/api/rest/equipment/supply-rate/" + aLong, SupplyRateDto.class);
            equipesAmount[0] += forObject.getAmount();
            return forObject;
        }).collect(Collectors.toList());

        Set<EquipmentDto> equipements = new LinkedHashSet<>();
        for (int i = 0; i < equipesAmount[0]; i++) {
            for (SupplyRateDto supplyRateDto : supplyRatesDtos) {
                List<EquipmentDto> filteredEquipesByType = filterEquipesByType(equipementsDtos, supplyRateDto);
                for (EquipmentDto equipmentDto : filteredEquipesByType) {
                    if ((equipmentDto.getUser() == null && (equipmentDto.getDecommissioningDate() == null || equipmentDto.getDecommissioningDate().isBefore(LocalDate.now())))) {
                        if (isExistByType(equipements, equipmentDto) == false) {
                            equipements.add(equipmentDto);
                        }
                    } else {
                        if ((isExistByType(notExistedEqipes, equipmentDto) == false)) {
                            notExistedEqipes.add(equipmentDto);
                        }
                    }
                }
            }
        }

        for (EquipmentDto equipmentDto : equipements) {
            notExistedEqipes.forEach(equipmentDto1 -> {
                if (equipmentDto.getType().equals(equipmentDto1.getType()))
                    notExistedEqipes.remove(equipmentDto1);
            });
        }

        if (equipements.size() == equipesAmount[0]) {
            delegateExecution.setVariable("equipes", equipements);
            delegateExecution.setVariable("isDistributed", Boolean.valueOf("true"));
            notExistedEqipes.clear();
        } else {
            delegateExecution.setVariable("notExistedEqipes", notExistedEqipes);
            delegateExecution.setVariable("isDistributed", Boolean.valueOf("false"));
        }
    }

    private boolean isExistByType(Set<EquipmentDto> equipements, EquipmentDto equipmentDto) {
        Optional<EquipmentDto> optionalEquipmentDto = equipements.stream().filter(dto -> dto.getType().equals(equipmentDto.getType())).findAny();
        return optionalEquipmentDto.isPresent();
    }

    private List<EquipmentDto> filterEquipesByType(List<EquipmentDto> equipementsDtos, SupplyRateDto supplyRateDto) {
        return equipementsDtos.stream().filter(equipmentDto -> {
            return equipmentDto.getType().equals(supplyRateDto.getEquipmentType());
        }).collect(Collectors.toList());
    }
}
