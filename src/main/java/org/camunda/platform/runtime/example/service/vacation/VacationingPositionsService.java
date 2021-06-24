package org.camunda.platform.runtime.example.service.vacation;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.vacation.dto.OrgUnitDto;
import org.camunda.platform.runtime.example.service.vacation.dto.VacationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class VacationingPositionsService implements JavaDelegate {

    private final RestService restService;

    @Autowired
    public VacationingPositionsService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long orgUnitId = (Long) delegateExecution.getVariable(ProcessVariables.ORG_UNIT_ID);
        List<PositionDto> allPositions = (List<PositionDto>) delegateExecution.getVariable(ProcessVariables.ALL_POSITIONS);
        OrgUnitDto unitDto = restService.getById(ApiRoutes.UNIT, orgUnitId, OrgUnitDto.class);
        List<VacationDto> vacationsDtos = restService.getAll(ApiRoutes.VACATION, VacationDto[].class);

        PositionDto directorsPosition = findDirectorsPosition(allPositions);
        PositionDto leadPosition = findLeadsPosition(unitDto.getPositions(), allPositions);

        if (isVacation(leadPosition, vacationsDtos)) {
            delegateExecution.setVariable(ProcessVariables.LEAD_POSITION_ID, directorsPosition.getId());
            delegateExecution.setVariable(ProcessVariables.LEAD_POSITION_NAME, directorsPosition.getTitle());
        } else {
            delegateExecution.setVariable(ProcessVariables.LEAD_POSITION_ID, leadPosition.getId());
            delegateExecution.setVariable(ProcessVariables.LEAD_POSITION_NAME, leadPosition.getTitle());
        }

        delegateExecution.setVariable(ProcessVariables.DIRECTORS_POSITION_ID, directorsPosition.getId());
    }

    private PositionDto findDirectorsPosition(List<PositionDto> positions) {
        return getWithMinIndexPosition(positions);
    }

    private PositionDto findLeadsPosition(List<Long> positionsIds, List<PositionDto> allPositions) {
        List<PositionDto> unitsPositions = new ArrayList<>();
        positionsIds.forEach(id -> {
            unitsPositions.add(allPositions.stream().filter(positionDto -> positionDto.getId().equals(id)).findFirst().get());
        });
        return getWithMinIndexPosition(unitsPositions);
    }

    private PositionDto getWithMinIndexPosition(List<PositionDto> unitsPositions) {
        List<Integer> positionsIndexes = new ArrayList<>();
        unitsPositions.forEach(positionDto -> positionsIndexes.add(positionDto.getIndex()));
        int minIdex = Collections.min(positionsIndexes);
        return unitsPositions.stream().filter(positionDto -> positionDto.getIndex() == minIdex).findFirst().get();
    }

    private boolean isVacation(PositionDto positionDto, List<VacationDto> employees) {
        return employees.stream().anyMatch(vacationDto -> {
            if (vacationDto.getEmployee().equals(positionDto.getEmployee()))
                return true;
            return false;
        });
    }
}
