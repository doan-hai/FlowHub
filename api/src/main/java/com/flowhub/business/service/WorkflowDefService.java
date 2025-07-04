package com.flowhub.business.service;

import com.flowhub.base.data.ResponsePage;
import com.flowhub.business.dto.input.WorkflowDefInput;
import com.flowhub.business.dto.output.WorkflowDefOutput;
import com.flowhub.business.enums.Status;

/**
 * @author haidv
 * @version 1.0
 */
public interface WorkflowDefService {

  ResponsePage<WorkflowDefOutput> getWorkFlowDefines(String name, Status status);

  WorkflowDefOutput getWorkFlowDefine(Long id);

  void createWorkflow(WorkflowDefInput workflowDefInput);

  void updateWorkflow(String id, WorkflowDefInput workflowDefInput);
}
