package com.flowhub.business.repository.db;

import com.flowhub.business.entity.WorkflowDef;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface WorkflowDefRepository extends JpaRepository<WorkflowDef, String> {

  Optional<WorkflowDef> findByDeletedFalseAndWorkflowDefName(String name);

  Optional<WorkflowDef> findByDeletedFalseAndId(Long id);
}
