package org.camunda.platform.runtime.example.service.equipement;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class OfficeEquipementsService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Value("${erp.base.url}")
    private String baseUrl;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<EquipmentDto> notExistedEqipes = (List<EquipmentDto>) delegateExecution.getVariable("notExistedEqipes");
        Long price = (Long) delegateExecution.getVariable("price");

        notExistedEqipes.forEach(equipmentDto -> {
            EquipmentDto dto = new EquipmentDto();
            dto.setSerialNumber(equipmentDto.getType() + "SN" + ThreadLocalRandom.current().nextLong(4000));
            dto.setType(equipmentDto.getType());
            dto.setPurchasePrice(BigDecimal.valueOf(price / notExistedEqipes.size()));
            dto.setCommissioningDate(LocalDate.now());
            this.restTemplate.postForObject(baseUrl + "/api/rest/equipment", dto, EquipmentDto.class);
        });
    }
}
