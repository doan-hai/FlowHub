package com.flowhub.business.repository.db;

import com.flowhub.business.entity.Task;
import com.flowhub.business.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author haidv
 * @version 1.0
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

  Optional<Task> findByDeletedFalseAndIdAndTaskStatus(Long id, TaskStatus taskStatus);

  List<Task> findByDeletedFalseAndTaskDefIdInAndTaskStatusIn(List<String> ids, List<TaskStatus> taskStatuses);

  Page<Task> findByDeletedFalseAndWaitingLessThanAndTaskStatusOrderByCreatedAtDesc(Long id, TaskStatus taskStatus, Pageable pageable);
}
