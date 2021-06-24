package org.camunda.platform.runtime.example.service.position;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ApiRoutes;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.rest.dto.Dto;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FreePositionsService implements JavaDelegate {

    private final RestService restService;

    @Autowired
    public FreePositionsService(RestService restService) {
        this.restService = restService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<PositionDto> allPositions = restService.getAll(ApiRoutes.POSITION, PositionDto[].class);
        List<PositionDto> freePositions = allPositions.stream().filter(position -> position.getEmployee() == null).collect(Collectors.toList());

        delegateExecution.setVariable(ProcessVariables.ALL_POSITIONS, allPositions);
        delegateExecution.setVariable(ProcessVariables.FREE_POSITIONS, freePositions);
        delegateExecution.setVariable(ProcessVariables.FREE_POSITIONS_NAMES, Spin.JSON(extractPositionsName(freePositions)));
    }

    private Map<Long, String> extractPositionsName(List<PositionDto> positionDtos) {
        Map<Long, String> map = new HashMap<>();
        if (positionDtos.isEmpty()) {
            map.put(-1l, "Vacation not available");
        } else {
            positionDtos.forEach(positionDto -> {
                PositionDto dto = positionDto;
                map.put(dto.getId(), dto.getTitle());
            });
        }
        return map;
    }
}
