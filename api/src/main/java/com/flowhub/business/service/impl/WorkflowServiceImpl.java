package com.flowhub.business.service.impl;

import com.flowhub.base.data.BaseService;
import com.flowhub.business.dto.input.WorkflowDefInput;
import com.flowhub.business.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author haidv
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl extends BaseService implements WorkflowService {

  @Override
  public void createWorkflow(WorkflowDefInput workflowDefInput) {}

  @Override
  public void updateWorkflow(String id, WorkflowDefInput workflowDefInput) {}
}
