package org.camunda.platform.runtime.example.service.equipement;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.camunda.platform.runtime.example.service.equipement.dto.SupplyRateDto;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfficeEquipmentsDistributionService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID);
        PositionDto positionDto = this.restTemplate.getForObject(ApiRoutes.POSITION + positionId, PositionDto.class);
        List<EquipmentDto> equipementsDtos = Arrays.asList(restTemplate.getForEntity(ApiRoutes.EQUIPEMENT, EquipmentDto[].class).getBody());

        final int[] equipesAmount = {0};
        List<SupplyRateDto> supplyRatesDtos = positionDto.getSupplyRates().stream().map(id -> {
            SupplyRateDto forObject = this.restTemplate.getForObject(ApiRoutes.SUPPLY_RATE + id, SupplyRateDto.class);
            equipesAmount[0] += forObject.getAmount();
            return forObject;
        }).collect(Collectors.toList());

        List<EquipmentDto> existedEquipements = new ArrayList<>();
        List<EquipmentDto> notExistedEqipes = new ArrayList<>();

        for (int i = 0; i < equipesAmount[0]; i++) {
            for (SupplyRateDto supplyRateDto : supplyRatesDtos) {
                List<EquipmentDto> filteredEquipesByType = filterEquipesByType(equipementsDtos, supplyRateDto);
                for (EquipmentDto equipmentDto : filteredEquipesByType) {
                    if ((equipmentDto.getUser() == null && (equipmentDto.getDecommissioningDate() == null || equipmentDto.getDecommissioningDate().isBefore(LocalDate.now())))) {
                        if (isExistByType(existedEquipements, equipmentDto) == false) {
                            existedEquipements.add(equipmentDto);
                        }
                    } else {
                        if (isExistByType(notExistedEqipes, equipmentDto) == false) {
                            notExistedEqipes.add(equipmentDto);
                        }
                    }
                }
            }
        }

        for (EquipmentDto equipmentDto : existedEquipements) {
            Collection<EquipmentDto> filteredCollection = notExistedEqipes
                    .stream()
                    .filter(next -> next.getType().equals(equipmentDto.getType()))
                    .collect(Collectors.toList());
            notExistedEqipes.removeAll(filteredCollection);
        }

        if (existedEquipements.size() == equipesAmount[0]) {
            delegateExecution.setVariable(ProcessVariables.EQUIPES, existedEquipements);
            delegateExecution.setVariable(ProcessVariables.IS_DISTRIBUTED, true);
        } else {
            delegateExecution.setVariable(ProcessVariables.NOT_EXISTED_EQUIPES, notExistedEqipes);
            delegateExecution.setVariable(ProcessVariables.NOT_EXISTED_EQUIPES_NAMES, collectEquipesNames(notExistedEqipes, supplyRatesDtos));
            delegateExecution.setVariable(ProcessVariables.IS_DISTRIBUTED, false);
        }
    }

    private boolean isExistByType(List<EquipmentDto> equipements, EquipmentDto equipmentDto) {
        Optional<EquipmentDto> optionalEquipmentDto = equipements.stream().filter(dto -> dto.getType().equals(equipmentDto.getType())).findAny();
        return optionalEquipmentDto.isPresent();
    }

    private List<EquipmentDto> filterEquipesByType(List<EquipmentDto> equipementsDtos, SupplyRateDto supplyRateDto) {
        return equipementsDtos.stream().filter(equipmentDto -> {
            return equipmentDto.getType().equals(supplyRateDto.getEquipmentType());
        }).collect(Collectors.toList());
    }

    private String collectEquipesNames(List<EquipmentDto> notExistedEqipes, List<SupplyRateDto> supplyRateDtos) {
        StringBuffer equipesNames = new StringBuffer();
        for (EquipmentDto equipmentDto : notExistedEqipes) {
            Optional<SupplyRateDto> first = supplyRateDtos.stream()
                    .filter(supplyRateDto -> supplyRateDto.getEquipmentType().equals(equipmentDto.getType()))
                    .findAny();
            if (first.isPresent())
                equipesNames.append(first.get().getTitle() + ", ");
        }
        return equipesNames.toString();
    }
}
