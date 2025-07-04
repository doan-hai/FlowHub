package com.flowhub.business.repository.db;

import com.flowhub.business.entity.Workflow;
import com.flowhub.business.enums.WorkflowStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String> {

  Optional<Workflow> findByDeletedFalseAndId(Long id);

  boolean existsByDeletedFalseAndIdAndWorkflowStatus(Long id, WorkflowStatus workflowStatus);

  @Query(
      "select count(wf.id) > 0 from Workflow wf " +
          "join TaskDef td on wf.workflowDefId = td.workflowDefId " +
          "left join Task t on td.id = t.taskDefId and t.taskStatus in (com.flowhub.enums.TaskStatus.COMPLETED, com.flowhub.enums.TaskStatus.COMPLETED_WITH_ERRORS) " +
          "where wf.id = ?1 and t.id is null")
  boolean existsByCompletedAllTask(Long workflowId);
}
