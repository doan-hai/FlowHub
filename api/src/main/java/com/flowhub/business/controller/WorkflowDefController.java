package com.flowhub.business.controller;

import com.flowhub.base.data.ResponseData;
import com.flowhub.base.data.ResponsePage;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.business.dto.input.WorkflowDefInput;
import com.flowhub.business.dto.output.WorkflowDefOutput;
import com.flowhub.business.enums.Status;
import com.flowhub.business.service.WorkflowDefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haidv
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
public class WorkflowDefController {

  private final WorkflowDefService workflowDefService;

  @Operation(summary = "Lấy danh sách các mẫu luồng công việc")
  @GetMapping("/workflow-defines")
  public ResponseEntity<ResponseData<ResponsePage<WorkflowDefOutput>>> getWorkFlowDefines(
      @Schema(description = "Tên mẫu luồng công việc") @RequestParam(
          required = false) String name,
      @Schema(description = "Traạng thái mẫu luồng công việc") @RequestParam(
          required = false) Status status) {
    return ResponseUtils.success(workflowDefService.getWorkFlowDefines(name, status));
  }

  @Operation(summary = "Lấy thông tin chi tiết của một mẫu luồng công việc")
  @GetMapping("/workflow-defines/{id}")
  public ResponseEntity<ResponseData<WorkflowDefOutput>> getWorkFlowDefine(
      @PathVariable Long id) {
    return ResponseUtils.success(workflowDefService.getWorkFlowDefine(id));
  }

  @Operation(summary = "Tạo mới một mẫu luồng công việc")
  @PostMapping("/workflow-defines")
  public ResponseEntity<ResponseData<Void>> createWorkflowDefines(
      @RequestBody WorkflowDefInput workflowDefInput) {
    workflowDefService.createWorkflow(workflowDefInput);
    return ResponseUtils.success();
  }

  @Operation(summary = "Cập nhật một mẫu luồng công việc")
  @PutMapping("/workflow-defines/{id}")
  public ResponseEntity<ResponseData<Void>> updateWorkflow(
      @PathVariable String id, @RequestBody WorkflowDefInput workflowDefInput) {
    workflowDefService.updateWorkflow(id, workflowDefInput);
    return ResponseUtils.success();
  }
}
