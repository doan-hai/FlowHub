package com.flowhub.business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.flowhub.base.data.BaseEntity;
import com.flowhub.base.data.StringListConverter;
import com.flowhub.business.enums.TaskType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * @author haidv
 * @version 1.0
 */
@Entity
@Table(name = "tbl_task_def")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDef extends BaseEntity {

  @Column(name = "workflow_def_id")
  private Long workflowDefId;

  @Column(name = "workflow_def_name")
  private String workflowDefName;

  @Column(name = "task_def_name")
  private String taskDefName;

  @Column(name = "expression_def")
  private String expressionDef;

  @Column(name = "waiting")
  private Long waiting;

  @Column(name = "description")
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "required_input_parameters")
  private List<String> requiredInputParameters = new ArrayList<>();

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "required_output_parameters")
  private List<String> requiredOutputParameters = new ArrayList<>();

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TaskType type = TaskType.SWITCH;

  @Column(name = "sink")
  private String sink;

  @Column(name = "previous_task_id")
  @Convert(converter = StringListConverter.class)
  private List<String> previousTaskId = new ArrayList<>();
}
