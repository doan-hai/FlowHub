package com.flowhub.business.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.flowhub.base.exception.AbstractError;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ErrorDef implements AbstractError {
  ;

  private final String errorCode;

  private final HttpStatus httpStatus;
}
