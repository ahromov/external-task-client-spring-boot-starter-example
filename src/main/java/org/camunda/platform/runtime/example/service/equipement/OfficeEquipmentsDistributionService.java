package org.camunda.platform.runtime.example.service.equipement;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.rest.dto.Dto;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.camunda.platform.runtime.example.service.equipement.dto.SupplyRateDto;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OfficeEquipmentsDistributionService implements JavaDelegate {

    private final RestService restService;

    @Autowired
    public OfficeEquipmentsDistributionService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID);

        PositionDto positionDto = restService.getById(ApiRoutes.POSITION, positionId, PositionDto.class);
        List<SupplyRateDto> positionsSupplyRatesDtos = getSupplyRateDtos(positionDto);
        int neededPositionsEquipementsAmount = getAmount(positionsSupplyRatesDtos);

        List<Dto> allEquipementsDtos = restService.getAll(ApiRoutes.EQUIPEMENT, EquipmentDto[].class);

        List<EquipmentDto> existedEqipes = (List<EquipmentDto>) delegateExecution.getVariable(ProcessVariables.EXISTS_EQUIPES);
        if ((existedEqipes != null && !existedEqipes.isEmpty()) && existedEqipes.size() == neededPositionsEquipementsAmount) {
            delegateExecution.setVariable(ProcessVariables.EXISTS_EQUIPES, existedEqipes);
            delegateExecution.setVariable(ProcessVariables.IS_DISTRIBUTED, true);
        } else {
            existedEqipes = new ArrayList<>();
            List<EquipmentDto> notExistsEqipements = new ArrayList<>();

            for (int i = 0; i < neededPositionsEquipementsAmount; i++) {
                fillEquipementsLists(allEquipementsDtos, positionsSupplyRatesDtos, existedEqipes, notExistsEqipements);
            }

            if (existedEqipes.size() == neededPositionsEquipementsAmount && existedEqipes.size() < 0) {
                delegateExecution.setVariable(ProcessVariables.EXISTS_EQUIPES, existedEqipes);
                delegateExecution.setVariable(ProcessVariables.IS_DISTRIBUTED, true);
            } else {
                delegateExecution.setVariable(ProcessVariables.NOT_EXISTED_EQUIPES, notExistsEqipements);
                delegateExecution.setVariable(ProcessVariables.NOT_EXISTED_EQUIPES_NAMES, collectEquipesNames(notExistsEqipements, positionsSupplyRatesDtos));
                delegateExecution.setVariable(ProcessVariables.IS_DISTRIBUTED, false);
            }
        }
    }

    private int getAmount(List<SupplyRateDto> supplyRatesDtos) {
        int i = 0;
        for (SupplyRateDto supplyRateDto : supplyRatesDtos) {
            i += supplyRateDto.getAmount();
        }
        return i;
    }

    private void fillEquipementsLists(List<Dto> allEquipementsDtos, List<SupplyRateDto> supplyRatesDtos, List<EquipmentDto> existsEquipements, List<EquipmentDto> notExistedEqipes) {
        for (SupplyRateDto supplyRateDto : supplyRatesDtos) {
            List<EquipmentDto> filteredByTypeEquipements = new ArrayList<>();
            filteredByTypeEquipements.addAll(filterEquipementsByType(allEquipementsDtos, supplyRateDto));
            collectEquipementsLists(existsEquipements, notExistedEqipes, filteredByTypeEquipements);
        }
        removeExistedEquipements(existsEquipements, notExistedEqipes);
    }

    private void collectEquipementsLists(List<EquipmentDto> existsEquipements, List<EquipmentDto> notExistsEquipements, List<EquipmentDto> filteredByTypeEquipements) {
        for (EquipmentDto equipmentDto : filteredByTypeEquipements) {
            if (equipmentDto.getUser() == null
                    && (equipmentDto.getDecommissioningDate() == null
                    || equipmentDto.getDecommissioningDate().isBefore(LocalDate.now())
                    && isExists(existsEquipements, equipmentDto) == false)) {
                existsEquipements.add(equipmentDto);
            } else if (isExists(notExistsEquipements, equipmentDto) == false) {
                notExistsEquipements.add(equipmentDto);
            }
        }
    }

    private boolean isExists(List<EquipmentDto> equipements, EquipmentDto equipmentDto) {
        Optional<EquipmentDto> optionalEquipmentDto = equipements.stream().filter(dto -> dto.getType().equals(equipmentDto.getType())).findAny();
        return optionalEquipmentDto.isPresent();
    }

    private void removeExistedEquipements(List<EquipmentDto> existsEquipements, List<EquipmentDto> notExistedEqipes) {
        for (EquipmentDto equipmentDto : existsEquipements) {
            Collection<EquipmentDto> filteredByTypeOfExistsCollection = notExistedEqipes
                    .stream()
                    .filter(next -> next.getType().equals(equipmentDto.getType()))
                    .collect(Collectors.toList());
            notExistedEqipes.removeAll(filteredByTypeOfExistsCollection);
        }
    }

    private List<SupplyRateDto> getSupplyRateDtos(PositionDto positionDto) {
        List<SupplyRateDto> supplyRatesDtos = positionDto.getSupplyRates().stream()
                .map(id -> {
                    SupplyRateDto forObject = restService.getById(ApiRoutes.SUPPLY_RATE, id, SupplyRateDto.class);
                    return forObject;
                }).collect(Collectors.toList());
        return supplyRatesDtos;
    }

    private List<EquipmentDto> filterEquipementsByType(List<Dto> equipementsDtos, SupplyRateDto supplyRateDto) {
        List<EquipmentDto> equipmentDtoList = new ArrayList<>();
        EquipmentDto e = (EquipmentDto) equipementsDtos.stream()
                .filter(equipmentDto -> ((EquipmentDto) equipmentDto).getType().equals(supplyRateDto.getEquipmentType())).findAny().get();
        equipmentDtoList.add(e);
        return equipmentDtoList;
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
