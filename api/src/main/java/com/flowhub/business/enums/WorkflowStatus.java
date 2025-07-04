package com.flowhub.business.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum WorkflowStatus {
  RUNNING(false, false),
  COMPLETED(true, true),
  FAILED(true, false),
  TERMINATED(true, false),
  PAUSED(false, true);

  private final boolean terminal;

  private final boolean successful;
}
