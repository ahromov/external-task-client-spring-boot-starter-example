package org.camunda.platform.runtime.example.service.erp;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.erp.dto.EmployeeDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentAssignDto;
import org.camunda.platform.runtime.example.service.equipement.dto.EquipmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ErpService implements JavaDelegate {

    private final RestService restService;

    @Autowired
    public ErpService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long candidatesPositionId = (Long) delegateExecution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID);
        String candidatesLastName = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_LAST_NAME);
        String candidatesFirstName = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_FIRST_NAME);
        String candidatesLogin = (String) delegateExecution.getVariable(ProcessVariables.CANDIDATE_LOGIN);
        LocalDate candidatesBirthDate = toLocalDate((Date) delegateExecution.getVariable(ProcessVariables.CANDIDATE_BIRTH_DATE));
        LocalDate candidatesHiringDate = toLocalDate((Date) delegateExecution.getVariable(ProcessVariables.CANDIDATE_EMPLOYMENT_DATE));

        EmployeeDto emloyee = newEmployee(candidatesPositionId, candidatesLastName, candidatesFirstName, candidatesLogin, candidatesBirthDate, candidatesHiringDate);
        emloyee = restService.create(ApiRoutes.EMPLOYEE, emloyee, EmployeeDto.class);

        List<EquipmentDto> equipements = (List<EquipmentDto>) delegateExecution.getVariable(ProcessVariables.EXISTS_EQUIPES);
        createEquipements(emloyee, equipements);
    }

    private EmployeeDto newEmployee(Long positionId, String lastName, String firstName, String login, LocalDate birthDate, LocalDate employmentDate) {
        EmployeeDto emloyee = new EmployeeDto();
        emloyee.setPosition(positionId);
        emloyee.setLastName(lastName);
        emloyee.setFirstName(firstName);
        emloyee.setBirthDate(birthDate);
        emloyee.setHireDate(employmentDate);
        emloyee.setLogin(login);
        return emloyee;
    }

    private void createEquipements(EmployeeDto newEmployeeDto, List<EquipmentDto> equipements) {
        if (equipements != null) {
            equipements.forEach(equip -> {
                EquipmentAssignDto assignDto = new EquipmentAssignDto();
                assignDto.setSerialNumber(equip.getSerialNumber());
                assignDto.setEmployeeId(newEmployeeDto.getId());
                restService.update(ApiRoutes.EQUIPEMENT + equip.getSerialNumber() + "/assign", assignDto, EquipmentDto.class);
            });
        }
    }

    public LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
