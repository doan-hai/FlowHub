package com.flowhub.business.service.impl;

import com.flowhub.base.data.BaseService;
import com.flowhub.base.event.MessageData;
import com.flowhub.base.event.MessageInterceptor;
import com.flowhub.business.dto.message.WorkflowMessage;
import com.flowhub.business.entity.Task;
import com.flowhub.business.entity.TaskDef;
import com.flowhub.business.entity.Workflow;
import com.flowhub.business.enums.TaskStatus;
import com.flowhub.business.enums.TaskType;
import com.flowhub.business.enums.WorkflowStatus;
import com.flowhub.business.repository.db.TaskDefRepository;
import com.flowhub.business.repository.db.TaskRepository;
import com.flowhub.business.repository.db.WorkflowDefRepository;
import com.flowhub.business.repository.db.WorkflowRepository;
import com.flowhub.business.service.DeciderService;
import com.flowhub.business.utils.Utils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author haidv
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeciderServiceImpl extends BaseService implements DeciderService {

  private final WorkflowRepository workflowRepository;

  private final WorkflowDefRepository workflowDefRepository;

  private final TaskRepository taskRepository;

  private final TaskDefRepository taskDefRepository;

  private final MessageInterceptor messageInterceptor;

  @Transactional
  @Override
  public void startWorkflow(WorkflowMessage workflowMessage) {
    workflowDefRepository
        .findByDeletedFalseAndWorkflowDefName(workflowMessage.getWorkflowDefName())
        .ifPresentOrElse(
            workflowDef -> {
              Workflow workflow =
                  workflowRepository.save(
                      Workflow.builder()
                              .correlationId(workflowMessage.getCorrelationId())
                              .workflowStatus(WorkflowStatus.RUNNING)
                              .inputParameters(workflowMessage.getInputParameters())
                              .startedAt(LocalDateTime.now())
                              .workflowDefId(workflowDef.getId())
                              .workflowDefName(workflowMessage.getWorkflowDefName())
                              .build());
              List<TaskDef> nextTaskDefs =
                  taskDefRepository.findByDeletedFalseAndWorkflowDefIdAndPreviousTaskIdIsNull(
                      workflowDef.getId());
              if (nextTaskDefs.isEmpty()) {
                log.error(
                    "Not found task start workflow: {}", workflowMessage.getWorkflowDefName());
                workflow.setEndedAt(LocalDateTime.now());
                workflow.setWorkflowStatus(WorkflowStatus.COMPLETED);
                workflowRepository.save(workflow);
              } else {
                for (TaskDef nextTaskDef : nextTaskDefs) {
                  Map<String, Object> rawInputParameters = new HashMap<>();
                  if (!CollectionUtils.isEmpty(workflowMessage.getInputParameters())) {
                    rawInputParameters.putAll(workflowMessage.getInputParameters());
                  }
                  if (!CollectionUtils.isEmpty(workflowDef.getInputTemplate())) {
                    rawInputParameters.putAll(workflowDef.getInputTemplate());
                  }
                  this.saveAndPushNextEvent(
                      nextTaskDef,
                      workflowMessage.getCorrelationId(),
                      workflow.getId(),
                      rawInputParameters);
                }
              }
            },
            () ->
                log.error(
                    "Not found workflow with name: {}", workflowMessage.getWorkflowDefName()));
  }

  @Transactional
  @Override
  public void finishTask(WorkflowMessage taskMessage) {
    taskRepository
        .findByDeletedFalseAndIdAndTaskStatus(taskMessage.getTaskId(), TaskStatus.IN_PROGRESS)
        .ifPresentOrElse(
            currentTask -> {
              if (workflowRepository.existsByDeletedFalseAndIdAndWorkflowStatus(
                  currentTask.getWorkflowId(), WorkflowStatus.RUNNING)) {
                currentTask.setWorkerName(taskMessage.getWorkerName());
                currentTask.setEndedAt(LocalDateTime.now());
                currentTask.setTaskStatus(taskMessage.getTaskStatus());
                currentTask.setOutputParameters(taskMessage.getOutputParameters());
                taskRepository.saveAndFlush(currentTask);
                taskDefRepository
                    .findByDeletedFalseAndId(currentTask.getTaskDefId())
                    .ifPresentOrElse(
                        currentTaskDef -> {
                          if (TaskStatus.COMPLETED.equals(taskMessage.getTaskStatus())) {
                            this.handelCompleteTask(currentTask, taskMessage);
                          }
                        },
                        () ->
                            log.error(
                                "Not found taskDef with id: {}", currentTask.getTaskDefId()));
              } else {
                log.error(
                    "Workflow: {} with id: {} not running",
                    currentTask.getWorkflowDefName(),
                    currentTask.getWorkflowId());
              }
            },
            () -> log.error("Not found task in process with id: {}", taskMessage.getTaskId()));
  }

  private void handelCompleteTask(Task currentTask, WorkflowMessage taskMessage) {
    List<TaskDef> nextTaskDefs =
        taskDefRepository.findByDeletedFalseAndPreviousTaskId(
            Utils.makeLikeParameter(currentTask.getTaskDefId()));
    if (nextTaskDefs.isEmpty()) {
      log.info("Not found next taskDef workflow: {}", currentTask.getWorkflowDefName());
      this.completeWorkflow(currentTask.getWorkflowId());
    } else {
      workflowDefRepository
          .findByDeletedFalseAndId(currentTask.getWorkflowDefId())
          .ifPresentOrElse(
              workflowDef -> {
                for (TaskDef nextTaskDef : nextTaskDefs) {
                  Map<String, Object> rawInputParameters = new HashMap<>();
                  if (!CollectionUtils.isEmpty(taskMessage.getOutputParameters())) {
                    rawInputParameters.putAll(taskMessage.getOutputParameters());
                  }
                  if (!CollectionUtils.isEmpty(workflowDef.getInputTemplate())) {
                    rawInputParameters.putAll(workflowDef.getInputTemplate());
                  }
                  if (TaskType.SWITCH.equals(nextTaskDef.getType())
                      || TaskType.BROADCAST.equals(nextTaskDef.getType())) {
                    this.saveAndPushNextEvent(nextTaskDef, currentTask, rawInputParameters);
                  }
                  if (TaskType.JOIN.equals(nextTaskDef.getType())) {
                    List<Task> memberOnJoins =
                        taskRepository.findByDeletedFalseAndTaskDefIdInAndTaskStatusIn(
                            nextTaskDef.getPreviousTaskId(),
                            Arrays.asList(TaskStatus.COMPLETED, TaskStatus.COMPLETED_WITH_ERRORS));
                    for (Task memberOnJoin : memberOnJoins) {
                      if (!CollectionUtils.isEmpty(memberOnJoin.getOutputParameters())) {
                        rawInputParameters.putAll(memberOnJoin.getOutputParameters());
                      }
                    }
                    if (memberOnJoins.size() == nextTaskDef.getPreviousTaskId().size()) {
                      this.saveAndPushNextEvent(nextTaskDef, currentTask, rawInputParameters);
                    } else {
                      log.info("All member of: {} not completed", nextTaskDef.getTaskDefName());
                    }
                  }
                  if (TaskType.CASE_WHEN.equals(nextTaskDef.getType())) {
                    ExpressionParser parser = new SpelExpressionParser();
                    Expression exp = parser.parseExpression(nextTaskDef.getExpressionDef());
                    if (Boolean.TRUE.equals(
                        exp.getValue(currentTask.getOutputParameters(), Boolean.class))) {
                      this.saveAndPushNextEvent(nextTaskDef, currentTask, rawInputParameters);
                    }
                  }
                  if (TaskType.WAIT.equals(nextTaskDef.getType())) {
                    this.saveWaitEvent(
                        nextTaskDef,
                        currentTask.getCorrelationId(),
                        currentTask.getWorkflowId(),
                        rawInputParameters);
                  }
                }
              },
              () ->
                  log.error(
                      "Not found workflowDef with id: {}", currentTask.getWorkflowDefId()));
    }
  }

  private void completeWorkflow(Long id) {
    workflowRepository
        .findByDeletedFalseAndId(id)
        .ifPresentOrElse(
            workflow -> {
              if (!workflowRepository.existsByCompletedAllTask(id)) {
                workflow.setEndedAt(LocalDateTime.now());
                workflow.setWorkflowStatus(WorkflowStatus.COMPLETED);
                workflowRepository.save(workflow);
                log.info("Workflow with id: {} completed", id);
              } else {
                log.info("Workflow with id: {} not completed", id);
              }
            },
            () -> log.error("Not found workflow with id: {}", id));
  }

  private void saveAndPushNextEvent(
      TaskDef taskDef, Task currentTask, Map<String, Object> rawInputParameters) {
    this.saveAndPushNextEvent(
        taskDef, currentTask.getCorrelationId(), currentTask.getWorkflowId(), rawInputParameters);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void saveAndPushNextEvent(
      TaskDef taskDef,
      String correlationId,
      Long workflowId,
      Map<String, Object> rawInputParameters) {
    Map<String, Object> inputParameters = buildInputParameters(taskDef, rawInputParameters);
    Task task =
        taskRepository.save(
            Task.builder()
                .correlationId(correlationId)
                .workflowId(workflowId)
                .taskDefId(taskDef.getId())
                .taskDefName(taskDef.getTaskDefName())
                .workflowDefId(taskDef.getWorkflowDefId())
                .workflowDefName(taskDef.getWorkflowDefName())
                .taskStatus(TaskStatus.IN_PROGRESS)
                .inputParameters(inputParameters)
                .startedAt(LocalDateTime.now())
                .build());
    messageInterceptor.convertAndSend(
        taskDef.getSink(),
        new MessageData(
            String.format("%s:%s", taskDef.getWorkflowDefName(), correlationId),
            WorkflowMessage.builder()
                .taskId(task.getId())
                .taskDefName(taskDef.getTaskDefName())
                .inputParameters(inputParameters)
                .requiredOutputParameters(taskDef.getRequiredOutputParameters())
                .correlationId(correlationId)
                .taskStatus(TaskStatus.IN_PROGRESS)
                .build()));
  }

  private void saveWaitEvent(
      TaskDef taskDef,
      String correlationId,
      Long workflowId,
      Map<String, Object> rawInputParameters) {
    Map<String, Object> inputParameters = buildInputParameters(taskDef, rawInputParameters);
    taskRepository.save(
        Task.builder()
            .correlationId(correlationId)
            .workflowId(workflowId)
            .taskDefId(taskDef.getId())
            .taskDefName(taskDef.getTaskDefName())
            .workflowDefId(taskDef.getWorkflowDefId())
            .workflowDefName(taskDef.getWorkflowDefName())
            .waiting(System.currentTimeMillis() + taskDef.getWaiting())
            .taskStatus(TaskStatus.IN_PROGRESS)
            .inputParameters(inputParameters)
            .startedAt(LocalDateTime.now())
            .build());
  }

  private Map<String, Object> buildInputParameters(
      TaskDef taskDef, Map<String, Object> rawInputParameters) {
    Map<String, Object> inputParameters = new HashMap<>();
    if (!CollectionUtils.isEmpty(taskDef.getRequiredInputParameters())) {
      for (String requiredInputParameter : taskDef.getRequiredInputParameters()) {
        inputParameters.put(requiredInputParameter, rawInputParameters.get(requiredInputParameter));
      }
      if (inputParameters.size() != taskDef.getRequiredInputParameters().size()) {
        log.warn("Task: {} not enough inputParameters", taskDef.getTaskDefName());
      }
    }
    return inputParameters;
  }
}
