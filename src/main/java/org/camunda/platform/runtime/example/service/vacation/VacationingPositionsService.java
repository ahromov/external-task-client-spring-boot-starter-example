package org.camunda.platform.runtime.example.service.vacation;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.camunda.platform.runtime.example.service.vacation.dto.OrgUnitDto;
import org.camunda.platform.runtime.example.service.vacation.dto.VacationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class VacationingPositionsService implements JavaDelegate {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${erp.base.url}")
    private String baseUrl;

    private Long directorsPositionId = 11L;
    private String directorsPositionName = "Директор";

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long orgUnit = (Long) delegateExecution.getVariableTyped("orgUnit").getValue();
        OrgUnitDto unitDto = this.restTemplate.getForObject(baseUrl + "/api/rest/org-structure/unit/" + orgUnit, OrgUnitDto.class);
        PositionDto positionDto = this.restTemplate.getForObject(baseUrl + "/api/rest/org-structure/position/" + unitDto.getPositions().get(0), PositionDto.class);

        List<VacationDto> employees = Arrays.asList(restTemplate.getForEntity(baseUrl + "/api/rest/org-structure/vacation", VacationDto[].class).getBody());
        boolean isVacation = employees.stream().allMatch(vacationDto -> Long.valueOf(vacationDto.getEmployee()) == Long.valueOf(positionDto.getId()));
        if (isVacation) {
            setProcessVariables(delegateExecution, directorsPositionId, directorsPositionName);
        } else {
            setProcessVariables(delegateExecution, positionDto.getId(), positionDto.getTitle());
        }

        log.info("Processes variables", positionDto.getTitle());
    }

    private void setProcessVariables(DelegateExecution delegateExecution, Long directorsPositionId, String directorsPositionName) {
        delegateExecution.setVariable("leadId", directorsPositionId);
        delegateExecution.setVariable("lead", directorsPositionName);
    }
}
