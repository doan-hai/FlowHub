package com.flowhub.business.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.flowhub.business.enums.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
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
public class TaskDefInput {

  @Schema(description = "Tên của công việc", example = "Order Processing Task")
  private String taskDefName;

  @Schema(description = "Biểu thức định nghĩa công việc", example = "input.orderAmount > 100")
  @NotBlank(message = "Biểu thức định nghĩa công việc không được để trống")
  @Size(max = 1000, message = "Biểu thức định nghĩa công việc không được vượt quá 1000 ký tự")
  private String expressionDef;

  @Schema(description = "Thời gian chờ của công việc (tính bằng giây)", example = "60")
  private Long waiting;

  @Schema(description = "Mô tả của công việc", example = "This task processes orders")
  private String description;

  @Schema(description = "Danh sách các tham số đầu vào bắt buộc của công việc")
  private List<String> requiredInputParameters = new ArrayList<>();

  @Schema(description = "Danh sách các tham số đầu ra bắt buộc của công việc")
  private List<String> requiredOutputParameters = new ArrayList<>();

  @Schema(description = "Loại công việc", example = "SWITCH")
  @NotNull(message = "Loại công việc không được để trống")
  private TaskType type = TaskType.SWITCH;

  @Schema(description = "Tên của nơi xử lý công việc (topic kafka, http api)",
          example = "OrderProcessingWorker")
  private String sink;

  @Schema(description = "Danh sách các ID của công việc trước đó")
  private List<String> previousTaskId = new ArrayList<>();
}
