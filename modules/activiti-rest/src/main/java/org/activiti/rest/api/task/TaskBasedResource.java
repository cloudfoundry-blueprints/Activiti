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

package org.activiti.rest.api.task;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.TaskQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.DataResponse;
import org.activiti.rest.api.SecuredResource;
import org.activiti.rest.api.task.QueryVariable.QueryVariableOperation;
import org.restlet.data.Form;


/**
 * Shared logic for resources related to Tasks.
 * 
 * @author Frederik Heremans
 */
public class TaskBasedResource extends SecuredResource {
  
  private static HashMap<String, QueryProperty> properties = new HashMap<String, QueryProperty>();
  
  static {
    properties.put("id", TaskQueryProperty.TASK_ID);
    properties.put("name", TaskQueryProperty.NAME);
    properties.put("description", TaskQueryProperty.DESCRIPTION);
    properties.put("dueDate", TaskQueryProperty.DUE_DATE);
    properties.put("createTime", TaskQueryProperty.CREATE_TIME);
    properties.put("priority", TaskQueryProperty.PRIORITY);
    properties.put("executionId", TaskQueryProperty.EXECUTION_ID);
    properties.put("processInstanceId", TaskQueryProperty.PROCESS_INSTANCE_ID);
  }

  protected DelegationState getDelegationState(String delegationState) {
    DelegationState state = null;
    if(delegationState != null) {
      if(DelegationState.RESOLVED.name().toLowerCase().equals(delegationState)) {
        return DelegationState.RESOLVED;
      } else if(DelegationState.PENDING.name().toLowerCase().equals(delegationState)) {
        return DelegationState.PENDING;
      } else {
        throw new ActivitiIllegalArgumentException("Illegal value for delegationState: " + delegationState);
      }
    }
    return state;
  }
  
  /**
   * Populate the task based on the values that are present in the given {@link TaskRequest}.
   */
  protected void populateTaskFromRequest(Task task, TaskRequest taskRequest) {
    if(taskRequest.isNameSet()) {
      task.setName(taskRequest.getName());
    }
    if(taskRequest.isAssigneeSet()) {
      task.setAssignee(taskRequest.getAssignee());
    }
    if(taskRequest.isDescriptionSet()) {
      task.setDescription(taskRequest.getDescription());
    }
    if(taskRequest.isDuedateSet()) {
      task.setDueDate(taskRequest.getDueDate());
    }
    if(taskRequest.isOwnerSet()) {
      task.setOwner(taskRequest.getOwner());
    }
    if(taskRequest.isParentTaskIdSet()) {
      task.setParentTaskId(taskRequest.getParentTaskId());
    }
    if(taskRequest.isPrioritySet()) {
      task.setPriority(taskRequest.getPriority());
    }

    if(taskRequest.isDelegationStateSet()) {
      DelegationState delegationState = getDelegationState(taskRequest.getDelegationState());
      task.setDelegationState(delegationState);
    }
  }
  
  protected DataResponse getTasksFromQueryRequest(TaskQueryRequest request) {
    TaskQuery taskQuery = ActivitiUtil.getTaskService().createTaskQuery();
    Form query = getQuery();
    
    // Populate filter-parameters
    if(request.getName() != null) {
      taskQuery.taskName(request.getName());
    }
    if(request.getNameLike() != null) {
      taskQuery.taskNameLike(request.getNameLike());
    }
    if(request.getDescription() != null) {
      taskQuery.taskDescription(request.getDescription());
    }
    if(request.getDescriptionLike() != null) {
      taskQuery.taskDescriptionLike(request.getDescriptionLike());
    }
    if(request.getPriority() != null) {
      taskQuery.taskPriority(request.getPriority());
    }
    if(request.getMinimumPriority() != null) {
      taskQuery.taskMinPriority(request.getMinimumPriority());
    }
    if(request.getMaximumPriority() != null) {
      taskQuery.taskMaxPriority(request.getMaximumPriority());
    }
    if(request.getAssignee() != null) {
      taskQuery.taskAssignee(request.getAssignee());
    }
    if(request.getOwner() != null) {
      taskQuery.taskOwner(request.getOwner());
    }
    if(request.getUnassigned() != null) {
      taskQuery.taskUnassigned();
    }
    if(request.getDelegationState() != null) {
      DelegationState state = getDelegationState(request.getDelegationState());
      if(state != null) {
        taskQuery.taskDelegationState(state);
      }
    }
    if(request.getCandidateUser() != null) {
      taskQuery.taskCandidateUser(request.getCandidateUser());
    }
    if(request.getInvolvedUser() != null) {
      taskQuery.taskInvolvedUser(request.getInvolvedUser());
    }
    if(request.getCandidateGroup() != null) {
      taskQuery.taskCandidateGroup(request.getCandidateGroup());
    }
    if(request.getProcessInstanceId() != null) {
      taskQuery.processInstanceId(request.getProcessInstanceId());
    }
    if(request.getProcessInstanceBusinessKey() != null) {
      taskQuery.processInstanceBusinessKey(request.getProcessInstanceBusinessKey());
    }
    if(request.getExecutionId() != null) {
      taskQuery.executionId(request.getExecutionId());
    }
    if(request.getCreatedOn() != null) {
      taskQuery.taskCreatedOn(request.getCreatedOn());
    }
    if(request.getCreatedBefore() != null) {
      taskQuery.taskCreatedBefore(request.getCreatedBefore());
    }
    if(request.getCreatedAfter() != null) {
      taskQuery.taskCreatedAfter(request.getCreatedAfter());
    }
    if(request.getExcludeSubTasks() != null) {
      if(request.getExcludeSubTasks().booleanValue()) {
        taskQuery.excludeSubtasks();
      }
    }

    taskQuery.taskDefinitionKey(request.getTaskDefinitionKey());
    taskQuery.taskDefinitionKeyLike(request.getTaskDefinitionKeyLike());
    taskQuery.dueDate(request.getDueDate());
    taskQuery.dueBefore(request.getDueBefore());
    taskQuery.dueAfter(request.getDueAfter());
    
    if(request.getActive() != null) {
      if(request.getActive().booleanValue()) {
        taskQuery.active();
      } else {
        taskQuery.suspended();
      }
    }
    
    if(request.getTaskVariables() != null) {
      addTaskvariables(taskQuery, request.getTaskVariables());
    }
    
    if(request.getProcessVariables() != null) {
      addProcessvariables(taskQuery, request.getProcessVariables());
    }
    
    return new TaskPaginateList(this).paginateList(query, taskQuery, "id", properties);
  }
  
  protected void addTaskvariables(TaskQuery taskQuery, List<QueryVariable> variables) {
    
    for(QueryVariable variable : variables) {
      if(variable.getVariableOperation() == null) {
        throw new ActivitiIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
      }
      if(variable.getValue() == null) {
        throw new ActivitiIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
      }
      
      boolean nameLess = variable.getName() == null;
      
      Object actualValue = variable.getValue();
      if(variable.getType() != null) {
        // Perform explicit conversion instead of using raw value from request
        // TODO: use pluggable variable-creator based on objects and type
      }
      
      // A value-only query is only possible using equals-operator
      if(nameLess && variable.getVariableOperation() != QueryVariableOperation.EQUALS) {
        throw new ActivitiIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
      }
      
      switch(variable.getVariableOperation()) {
      
      case EQUALS:
        if(nameLess) {
          taskQuery.taskVariableValueEquals(actualValue);
        } else {
          taskQuery.taskVariableValueEquals(variable.getName(), actualValue);
        }
        break;
        
      case EQUALS_IGNORE_CASE:
        if(actualValue instanceof String) {
          taskQuery.taskVariableValueEqualsIgnoreCase(variable.getName(), (String)actualValue);
        } else {
          throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
        }
        break;
        
      case NOT_EQUALS:
        taskQuery.taskVariableValueNotEquals(variable.getName(), actualValue);
        break;
        
      case NOT_EQUALS_IGNORE_CASE:
        if(actualValue instanceof String) {
          taskQuery.taskVariableValueNotEqualsIgnoreCase(variable.getName(), (String)actualValue);
        } else {
          throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
        }
        break;
      default:
        throw new ActivitiIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
      }
    }
  }
  
protected void addProcessvariables(TaskQuery taskQuery, List<QueryVariable> variables) {
    
    for(QueryVariable variable : variables) {
      if(variable.getVariableOperation() == null) {
        throw new ActivitiIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
      }
      if(variable.getValue() == null) {
        throw new ActivitiIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
      }
      
      boolean nameLess = variable.getName() == null;
      
      Object actualValue = variable.getValue();
      if(variable.getType() != null) {
        // Perform explicit conversion instead of using raw value from request
        // TODO: use pluggable variable-creator based on objects and type
      }
      
      // A value-only query is only possible using equals-operator
      if(nameLess && variable.getVariableOperation() != QueryVariableOperation.EQUALS) {
        throw new ActivitiIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
      }
      
      switch(variable.getVariableOperation()) {
      
      case EQUALS:
        if(nameLess) {
          taskQuery.processVariableValueEquals(actualValue);
        } else {
          taskQuery.processVariableValueEquals(variable.getName(), actualValue);
        }
        break;
        
      case EQUALS_IGNORE_CASE:
        if(actualValue instanceof String) {
          taskQuery.processVariableValueEqualsIgnoreCase(variable.getName(), (String)actualValue);
        } else {
          throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
        }
        break;
        
      case NOT_EQUALS:
        taskQuery.processVariableValueNotEquals(variable.getName(), actualValue);
        break;
        
      case NOT_EQUALS_IGNORE_CASE:
        if(actualValue instanceof String) {
          taskQuery.processVariableValueNotEqualsIgnoreCase(variable.getName(), (String)actualValue);
        } else {
          throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
        }
        break;
      default:
        throw new ActivitiIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
      }
    }
  }
}
