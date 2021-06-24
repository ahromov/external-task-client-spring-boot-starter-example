package org.camunda.platform.runtime.example.service.equipement;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class OfficeEquipementsService implements JavaDelegate {

    private final RestService restService;

    @Autowired
    public OfficeEquipementsService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<EquipmentDto> notExistedEqipes = (List<EquipmentDto>) delegateExecution.getVariable(ProcessVariables.NOT_EXISTED_EQUIPES);
        List<EquipmentDto> existedEqipes = (List<EquipmentDto>) delegateExecution.getVariable(ProcessVariables.EXISTS_EQUIPES);
        Long price = (Long) delegateExecution.getVariable(ProcessVariables.EQUIPES_PRICE);

        if (existedEqipes == null)
            existedEqipes = new ArrayList<>();
        existedEqipes.addAll(createEqipements(notExistedEqipes, price));

        delegateExecution.setVariable(ProcessVariables.EXISTS_EQUIPES, existedEqipes);
    }

    private List<EquipmentDto> createEqipements(List<EquipmentDto> notExistedEqipes, Long price) {
        List<EquipmentDto> createdEquipements = new ArrayList<>();
        notExistedEqipes.forEach(equipmentDto -> {
            EquipmentDto dto = new EquipmentDto();
            dto.setSerialNumber(equipmentDto.getType() + "SN" + ThreadLocalRandom.current().nextLong(4000000));
            dto.setType(equipmentDto.getType());
            dto.setPurchasePrice(BigDecimal.valueOf(price / notExistedEqipes.size()));
            dto.setCommissioningDate(LocalDate.now());
            createdEquipements.add(restService.create(ApiRoutes.EQUIPEMENT, dto, EquipmentDto.class));
        });
        return createdEquipements;
    }
}
