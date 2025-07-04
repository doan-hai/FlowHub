package com.flowhub.business.dto.message;

import com.flowhub.business.enums.TaskStatus;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowMessage {

  private String correlationId;

  private String workflowDefName;

  private Map<String, Object> inputParameters;

  private Long taskId;

  private String taskDefName;

  private List<String> requiredOutputParameters;

  private Map<String, Object> outputParameters;

  private TaskStatus taskStatus;

  private String workerName;
}
