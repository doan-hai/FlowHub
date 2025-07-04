package com.flowhub.business.dto.output;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import com.flowhub.business.dto.input.TaskDefInput;
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
public class WorkflowDefOutput {

  @Schema(description = "ID của mẫu luồng công việc", example = "1")
  private String id;

  @Schema(description = "Tên mẫu luồng công việc", example = "Order Processing Workflow")
  private String workflowDefName;

  @Schema(description = "Mã định danh mẫu luồng công việc", example = "order-processing-workflow")
  private String workflowDefCode;

  @Schema(description = "Mô tả của schema mẫu luồng công việc",
          example = "This workflow handles order processing")
  private String description;

  @Schema(description = "Mẫu đầu vào của luồng công việc")
  private Map<String, Object> inputTemplate = new HashMap<>();

  @Schema(description = "Danh sách các task định nghĩa trong mẫu luồng công việc")
  private List<TaskDefInput> taskDefs = new ArrayList<>();

  @Schema(description = "Phiên bản của schema mẫu luồng công việc", example = "1")
  private int schemaVersion;
}
