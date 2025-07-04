package com.flowhub.business.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import com.flowhub.base.data.BaseEntity;
import java.util.HashMap;
import java.util.Map;
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
@Table(name = "tbl_workflow_def", indexes = {
    @Index(name = "idx_workflow_def_code", columnList = "workflow_def_code")
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDef extends BaseEntity {

  @Column(name = "workflow_def_name")
  private String workflowDefName;

  @Column(name = "workflow_def_code", unique = true)
  private String workflowDefCode;

  @Column(name = "description")
  private String description;

  @Column(name = "schema_version")
  private int schemaVersion;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "input_templates")
  private Map<String, Object> inputTemplate = new HashMap<>();
}
