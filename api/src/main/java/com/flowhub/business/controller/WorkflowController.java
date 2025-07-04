package com.flowhub.business.controller;

import com.flowhub.base.data.ResponseData;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.business.dto.input.WorkflowDefInput;
import com.flowhub.business.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haidv
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class WorkflowController {

  private final WorkflowService workflowService;

  @PostMapping("/workflows")
  public ResponseEntity<ResponseData<Void>> createWorkflow(
      @RequestBody WorkflowDefInput workflowDefInput) {
    workflowService.createWorkflow(workflowDefInput);
    return ResponseUtils.success();
  }

  @PutMapping("/workflows/{id}")
  public ResponseEntity<ResponseData<Void>> updateWorkflow(
      @PathVariable String id, @RequestBody WorkflowDefInput workflowDefInput) {
    workflowService.updateWorkflow(id, workflowDefInput);
    return ResponseUtils.success();
  }
}
