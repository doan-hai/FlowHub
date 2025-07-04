package com.flowhub.business.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;
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
public class WorkflowDefInput {

  @Schema(description = "Tên mẫu luồng công việc", example = "Order Processing Workflow")
  @NotBlank(message = "Tên mẫu luồng công việc không được để trống")
  @Size(max = 500, message = "Tên mẫu luồng công việc không được vượt quá 500 ký tự")
  private String workflowDefName;

  @Schema(description = "Mã định danh mẫu luồng công việc", example = "order-processing-workflow")
  @NotBlank(message = "Mã định danh mẫu luồng công việc không được để trống")
  @Size(max = 100, message = "Mã định danh mẫu luồng công việc không được vượt quá 100 ký tự")
  private String workflowDefCode;

  @Schema(description = "Mô tả của schema mẫu luồng công việc",
          example = "This workflow handles order processing")
  @Size(max = 1000, message = "Mô tả mẫu luồng công việc không được vượt quá 1000 ký tự")
  private String description;

  @Schema(description = "Mẫu đầu vào của luồng công việc")
  private Map<String, Object> inputTemplate = new HashMap<>();

  @Schema(description = "Danh sách các task định nghĩa trong mẫu luồng công việc")
  @NotEmpty(message = "Danh sách các task định nghĩa trong mẫu luồng công việc không được để trống")
  private List<TaskDefInput> taskDefs = new ArrayList<>();
}
