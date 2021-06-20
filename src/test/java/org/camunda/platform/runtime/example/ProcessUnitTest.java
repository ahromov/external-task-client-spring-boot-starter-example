package org.camunda.platform.runtime.example;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

public class ProcessUnitTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    @Deployment(resources = {"employmetn-process.bpmn"})
    public void executeProcess() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("loan_process");
        // run the process
        assertThat(processInstance).isActive();
        // just one flow should be there

    }
}
