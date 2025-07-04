package com.flowhub.business.job;

import com.flowhub.base.event.MessageData;
import com.flowhub.base.event.MessageInterceptor;
import com.flowhub.business.dto.message.TaskMessage;
import com.flowhub.business.entity.Task;
import com.flowhub.business.enums.TaskStatus;
import com.flowhub.business.repository.db.TaskDefRepository;
import com.flowhub.business.repository.db.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author haidv
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class WaitTask {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final TaskRepository taskRepository;

  private final TaskDefRepository taskDefRepository;

  private final MessageInterceptor messageInterceptor;

  @Value("${spring.application.name}")
  private String applicationName;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Scheduled(fixedDelay = 5000)
  public void waitTaskCompleted() {
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      var page =
          taskRepository.findByDeletedFalseAndWaitingLessThanAndTaskStatusOrderByCreatedAtDesc(
              System.currentTimeMillis(), TaskStatus.IN_PROGRESS, PageRequest.of(i, 1000));
      for (Task task : page.getContent()) {
        taskDefRepository
            .findByDeletedFalseAndId(task.getTaskDefId())
            .ifPresentOrElse(
                taskDef ->
                    messageInterceptor.convertAndSend(
                        taskDef.getSink(),
                        new MessageData(
                            String.format(
                                "%s:%s", task.getWorkflowDefName(), task.getCorrelationId()),
                            TaskMessage.builder()
                                       .taskId(task.getId())
                                       .taskDefName(task.getTaskDefName())
                                       .inputParameters(task.getInputParameters())
                                       .requiredOutputParameters(taskDef.getRequiredOutputParameters())
                                       .correlationId(task.getCorrelationId())
                                       .taskStatus(TaskStatus.COMPLETED)
                                       .workerName(applicationName)
                                       .build())),
                () -> log.error("Not found taskDef with id: {}", task.getTaskDefId()));
      }
      if (page.getTotalElements() < 1000) {
        break;
      }
    }
  }
}
