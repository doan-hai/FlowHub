package com.flowhub.business.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum TaskStatus {
  IN_PROGRESS(false, true, true),
  CANCELED(true, false, false),
  FAILED(true, false, true),
  FAILED_WITH_TERMINAL_ERROR(
      true, false, false), // No retries even if retries are configured, the task and the related
  // workflow should be terminated
  COMPLETED(true, true, true),
  COMPLETED_WITH_ERRORS(true, true, true),
  SCHEDULED(false, true, true),
  TIMED_OUT(true, false, true),
  SKIPPED(true, true, false);

  private final boolean terminal;

  private final boolean successful;

  private final boolean retriable;
}
