package org.camunda.platform.runtime.example.service.erp;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.erp.dto.EmployeeDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentAssignDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
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

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long positionId = (Long) delegateExecution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID);
        String lastName = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_LAST_NAME);
        String firstName = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_FIRST_NAME);
        String login = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_LOGIN);
        LocalDate birthDate = toLocalDate((Date) delegateExecution.getVariable(ProcessVariables.CANDIDATE_BIRTH_DATE));
        LocalDate employmentDate = toLocalDate((Date) delegateExecution.getVariable(ProcessVariables.CANDIDATE_EMPLOYMENT_DATE));

        EmployeeDto emloyee = new EmployeeDto();
        emloyee.setPosition(positionId);
        emloyee.setLastName(lastName);
        emloyee.setFirstName(firstName);
        emloyee.setBirthDate(birthDate);
        emloyee.setHireDate(employmentDate);
        emloyee.setLogin(login);
        EmployeeDto newEmployeeDto = this.restTemplate.postForObject(ApiRoutes.EMPLOYEE, emloyee, EmployeeDto.class);

        List<EquipmentDto> equipements = (List<EquipmentDto>) delegateExecution.getVariable(ProcessVariables.EQUIPES);
        if (equipements != null) {
            equipements.forEach(equip -> {
                EquipmentAssignDto assignDto = new EquipmentAssignDto();
                assignDto.setSerialNumber(equip.getSerialNumber());
                assignDto.setEmployeeId(newEmployeeDto.getId());
                this.restTemplate.patchForObject(ApiRoutes.EQUIPEMENT + equip.getSerialNumber() + "/assign", assignDto, EquipmentDto.class);
            });
        }
    }

    public LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
