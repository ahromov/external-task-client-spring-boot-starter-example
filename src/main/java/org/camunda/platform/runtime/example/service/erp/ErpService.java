package org.camunda.platform.runtime.example.service.erp;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.service.employment.dto.EmployeeDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ErpService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${erp.base.url}")
    private String baseUrl;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable("positionId");
        String lastName = (String) delegateExecution.getVariable("lastName");
        String firstName = (String) delegateExecution.getVariable("firstName");
        LocalDate birthDate = toLocalDate((Date) delegateExecution.getVariable("birthDate"));
        LocalDate employmentDate = toLocalDate((Date) delegateExecution.getVariable("employmentDate"));

        EmployeeDto emloyee = new EmployeeDto();
        emloyee.setPosition(positionId);
        emloyee.setLastName(lastName);
        emloyee.setFirstName(firstName);
        emloyee.setBirthDate(birthDate);
        emloyee.setHireDate(employmentDate);
        EmployeeDto newEmployeeDto = this.restTemplate.postForObject(baseUrl + "/api/rest/org-structure/employee", emloyee, EmployeeDto.class);

        List<String> equipIDs = (List<String>) delegateExecution.getVariable("equipIds");
        equipIDs.forEach(id -> {
            EquipmentDto equipmentDto = new EquipmentDto();
            equipmentDto.setSerialNumber(id);
            equipmentDto.setUser(newEmployeeDto.getId());
            this.restTemplate.postForObject(baseUrl + "/api/rest/equipment/" + id, equipmentDto, PositionDto.class);
        });
    }

    public LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
