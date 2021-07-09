package org.camunda.platform.runtime.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.platform.runtime.example.service.equipement.OfficeEquipmentsDistributionService;
import org.camunda.platform.runtime.example.service.rest.RestService;
import org.camunda.platform.runtime.example.service.rest.RestServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OfficeEquipementsDistridutionServiceTest {

    private DelegateExecution execution;
    private RestService restService;
    private OfficeEquipmentsDistributionService officeEquipmentsDistributionService;

    @Before
    public void setup() {
        execution = Mockito.mock(DelegateExecution.class);
        restService = new RestServiceImpl();
        officeEquipmentsDistributionService = new OfficeEquipmentsDistributionService(restService);
    }

    @Test
    public void testExecuteWhenIsDistributedFalse() throws Exception {
        // preconditions
        when(execution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID)).thenReturn(37L);

        // execute unit under test
        officeEquipmentsDistributionService.execute(execution);

        // postconditions
        verify(execution).setVariable(ProcessVariables.IS_DISTRIBUTED, false);
    }

    @Test
    public void testExecuteWhenIsDistributedTrue() throws Exception {
        // preconditions
        when(execution.getVariable(ProcessVariables.CANDIDATE_POSITION_ID)).thenReturn(35L);

        // execute unit under test
        officeEquipmentsDistributionService.execute(execution);

        // postconditions
        verify(execution).setVariable(ProcessVariables.IS_DISTRIBUTED, true);
    }
}
