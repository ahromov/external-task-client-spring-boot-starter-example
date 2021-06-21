/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.platform.runtime.example.service.position;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.platform.runtime.example.ProcessVariables;
import org.camunda.platform.runtime.example.service.position.dto.PositionDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PositionsService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long candidatesPositionId = (Long) delegateExecution.getVariable("FREE_POSITIONS_FORM");
        List<PositionDto> freePositionsDtos = (List<PositionDto>) delegateExecution.getVariable(ProcessVariables.FREE_POSITIONS);

        if (candidatesPositionId == null || candidatesPositionId == -1)
            throw new BpmnError("400", "Free positions is not available");

        Optional<PositionDto> positionDtos = freePositionsDtos.stream()
                .filter(positionDto1 -> positionDto1.getId().equals(candidatesPositionId)).findFirst();

        delegateExecution.setVariable(ProcessVariables.CANDIDATE_POSITION_ID, candidatesPositionId);
        delegateExecution.setVariable(ProcessVariables.CANDIDATE_POSITION_NAME, positionDtos.get().getTitle());
        delegateExecution.setVariable(ProcessVariables.ORG_UNIT_ID, positionDtos.get().getOrgUnit());
    }
}
