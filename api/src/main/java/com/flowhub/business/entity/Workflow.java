package com.flowhub.business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.flowhub.base.data.BaseEntity;
import com.flowhub.business.enums.WorkflowStatus;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

/**
 * @author haidv
 * @version 1.0
 */
@Entity
@Table(name = "tbl_workflow")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow extends BaseEntity {

  @Column(name = "correlation_id")
  private String correlationId;

  @Column(name = "workflow_def_id")
  private Long workflowDefId;

  @Column(name = "workflow_def_name")
  private String workflowDefName;

  @Column(name = "workflow_status")
  @Enumerated(EnumType.STRING)
  private WorkflowStatus workflowStatus;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "input_parameters")
  private Map<String, Object> inputParameters = new HashMap<>();

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "ended_at")
  private LocalDateTime endedAt;
}
