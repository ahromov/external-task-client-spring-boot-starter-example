package org.camunda.platform.runtime.example.service.erp;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.service.employment.dto.EmployeeDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentAssignDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ErpService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Value("${erp.base.url}")
    private String baseUrl;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable("positionId");
        String lastName = (String) delegateExecution.getVariable("lastName");
        String firstName = (String) delegateExecution.getVariable("firstName");
        String login = (String) delegateExecution.getVariable("login");
        LocalDate birthDate = toLocalDate((Date) delegateExecution.getVariable("birthDate"));
        LocalDate employmentDate = toLocalDate((Date) delegateExecution.getVariable("employmentDate"));

        EmployeeDto emloyee = new EmployeeDto();
        emloyee.setPosition(positionId);
        emloyee.setLastName(lastName);
        emloyee.setFirstName(firstName);
        emloyee.setBirthDate(birthDate);
        emloyee.setHireDate(employmentDate);
        emloyee.setLogin(login);
        EmployeeDto newEmployeeDto = this.restTemplate.postForObject(baseUrl + "/api/rest/org-structure/employee", emloyee, EmployeeDto.class);

        List<EquipmentDto> equips = (List<EquipmentDto>) delegateExecution.getVariable("equipes");
        if (equips != null) {
            equips.forEach(equip -> {
                EquipmentAssignDto assignDto = new EquipmentAssignDto();
                assignDto.setSerialNumber(equip.getSerialNumber());
                assignDto.setEmployeeId(newEmployeeDto.getId());
                this.restTemplate.patchForObject(baseUrl + "/api/rest/equipment/" + equip.getSerialNumber() + "/assign", assignDto, EquipmentDto.class);
            });
        }


    }

    public LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
