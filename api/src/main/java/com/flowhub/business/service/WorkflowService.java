package com.flowhub.business.service;

import com.flowhub.business.dto.input.WorkflowDefInput;

/**
 * @author haidv
 * @version 1.0
 */
public interface WorkflowService {

  void createWorkflow(WorkflowDefInput workflowDefInput);

  void updateWorkflow(String id, WorkflowDefInput workflowDefInput);
}
