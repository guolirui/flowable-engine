/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.impl.history.async.json.transformer;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.engine.impl.persistence.entity.HistoryJobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TaskPropertyChangedHistoryJsonTransformer extends AbstractNeedsTaskHistoryJsonTransformer {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskPropertyChangedHistoryJsonTransformer.class);

    public static final String TYPE = "task-property-changed";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        String taskId = getStringFromJson(historicalData, HistoryJsonConstants.ID);
        if (StringUtils.isNotEmpty(taskId)) {
            HistoricTaskInstanceEntity historicTaskInstance = commandContext.getHistoricTaskInstanceEntityManager().findById(taskId);

            Date lastUpdateTime = getDateFromJson(historicalData, HistoryJsonConstants.JOB_CREATE_TIME);
            if (historicTaskInstance.getLastUpdateTime() == null || !historicTaskInstance.getLastUpdateTime().after(lastUpdateTime)) {
                historicTaskInstance.setName(getStringFromJson(historicalData, HistoryJsonConstants.NAME));
                historicTaskInstance.setDescription(getStringFromJson(historicalData, HistoryJsonConstants.DESCRIPTION));
                historicTaskInstance.setAssignee(getStringFromJson(historicalData, HistoryJsonConstants.ASSIGNEE));
                historicTaskInstance.setOwner(getStringFromJson(historicalData, HistoryJsonConstants.OWNER));
                historicTaskInstance.setClaimTime(getDateFromJson(historicalData, HistoryJsonConstants.CLAIM_TIME));
                historicTaskInstance.setDueDate(getDateFromJson(historicalData, HistoryJsonConstants.DUE_DATE));
                historicTaskInstance.setPriority(getIntegerFromJson(historicalData, HistoryJsonConstants.PRIORITY));
                historicTaskInstance.setCategory(getStringFromJson(historicalData, HistoryJsonConstants.CATEGORY));
                historicTaskInstance.setFormKey(getStringFromJson(historicalData, HistoryJsonConstants.FORM_KEY));
                historicTaskInstance.setParentTaskId(getStringFromJson(historicalData, HistoryJsonConstants.PARENT_TASK_ID));
                historicTaskInstance.setTaskDefinitionKey(getStringFromJson(historicalData, HistoryJsonConstants.TASK_DEFINITION_KEY));
                historicTaskInstance.setProcessDefinitionId(getStringFromJson(historicalData, HistoryJsonConstants.PROCESS_DEFINITION_ID));
                historicTaskInstance.setLastUpdateTime(lastUpdateTime);
            
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("History job (id={}) has expired and will be ignored.", job.getId());
                }
            }
        }
    }

}
