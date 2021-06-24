package org.camunda.platform.runtime.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.platform.runtime.example.service.equipement.OfficeEquipmentsDistributionService;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.rest.RestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class OfficeEquipementsDistridutionServiceTest {

    private RestService restService = new RestServiceImpl();
    private OfficeEquipmentsDistributionService officeEquipmentsDistributionService = new OfficeEquipmentsDistributionService(restService);

    List<PositionDto> allFreePositions;

    @Before
    public void setup() {
        allFreePositions = restService.getAll(ApiRoutes.POSITION, PositionDto[].class);
    }

    @Test
    public void testExecute() throws Exception {
        // preconditions
        DelegateExecution execution = mock(DelegateExecution.class);

        if (!allFreePositions.isEmpty()) {
            PositionDto dto = allFreePositions.get(0);
            when(execution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID)).thenReturn(dto.getId().longValue());
        }

        // execute unit under test
        officeEquipmentsDistributionService.execute(execution);

        // postconditions
        verify(execution).setVariable(ProcessVariables.IS_DISTRIBUTED, false);
    }
}
