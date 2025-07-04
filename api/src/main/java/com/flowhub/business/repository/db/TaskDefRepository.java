package com.flowhub.business.repository.db;

import com.flowhub.business.entity.TaskDef;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface TaskDefRepository extends JpaRepository<TaskDef, String> {

  List<TaskDef> findByDeletedFalseAndWorkflowDefIdAndPreviousTaskIdIsNull(Long workflowId);

  Optional<TaskDef> findByDeletedFalseAndId(Long id);

  @Query(value = "select td.* from tbl_task_def td where td.is_deleted = false and td.previous_task_id like ?1", nativeQuery = true)
  List<TaskDef> findByDeletedFalseAndPreviousTaskId(String previousTaskId);
}
