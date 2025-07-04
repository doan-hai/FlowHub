package com.flowhub.business.service;

import com.flowhub.business.dto.message.WorkflowMessage;

/**
 * @author haidv
 * @version 1.0
 */
public interface DeciderService {

  void startWorkflow(WorkflowMessage workflowMessage);

  void finishTask(WorkflowMessage taskMessage);
}
