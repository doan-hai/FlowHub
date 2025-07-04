package com.flowhub.business.service.impl;

import com.flowhub.base.data.BaseService;
import com.flowhub.base.data.ResponsePage;
import com.flowhub.business.dto.input.WorkflowDefInput;
import com.flowhub.business.dto.output.WorkflowDefOutput;
import com.flowhub.business.enums.Status;
import com.flowhub.business.service.WorkflowDefService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author haidv
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowDefServiceImpl extends BaseService implements WorkflowDefService {

  @Override
  public ResponsePage<WorkflowDefOutput> getWorkFlowDefines(String name, Status status) {
    return null;
  }

  @Override
  public WorkflowDefOutput getWorkFlowDefine(Long id) {
    return null;
  }

  @Override
  public void createWorkflow(WorkflowDefInput workflowDefInput) {

  }

  @Override
  public void updateWorkflow(String id, WorkflowDefInput workflowDefInput) {

  }
}
