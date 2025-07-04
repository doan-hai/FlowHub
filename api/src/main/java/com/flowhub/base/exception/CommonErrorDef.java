package com.flowhub.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * **Enum `CommonErrorDef` - Định nghĩa mã lỗi chung trong hệ thống**
 *
 * <p>Enum này tập hợp các mã lỗi chuẩn trong hệ thống, giúp quản lý lỗi
 * một cách nhất quán và dễ dàng ánh xạ lỗi tới phản hồi API.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một lỗi xảy ra trong hệ thống, ứng dụng có thể sử dụng các mã lỗi
 * được định nghĩa trong `CommonErrorDef` để phản hồi API với thông tin cụ thể.</p>
 * <p>
 * **📌 Ví dụ sử dụng `CommonErrorDef` trong một exception:**
 * <pre>
 * {@code
 * throw new BaseException(CommonErrorDef.NOT_FOUND, "Không tìm thấy dữ liệu");
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ phản hồi JSON khi xảy ra lỗi:**
 * <pre>
 * {
 *     "errorCode": "CM_0004",
 *     "message": "Không tìm thấy dữ liệu",
 *     "httpStatus": 404
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public enum CommonErrorDef implements AbstractError {
  BAD_REQUEST("CM_0001", HttpStatus.BAD_REQUEST),
  UNAUTHORIZED("CM_0002", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("CM_0003", HttpStatus.FORBIDDEN),
  NOT_FOUND("CM_0004", HttpStatus.NOT_FOUND),
  METHOD_NOT_ALLOWED("CM_0005", HttpStatus.METHOD_NOT_ALLOWED),
  DATA_INTEGRITY_VIOLATION("CM_0006", HttpStatus.CONFLICT),
  MISSING_REQUEST_PARAMETER("CM_0007", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_MEDIA_TYPE("CM_0008", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  MEDIA_TYPE_NOT_ACCEPTABLE("CM_0009", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
  ARGUMENT_TYPE_MISMATCH("CM_0010", HttpStatus.BAD_REQUEST),
  ARGUMENT_NOT_VALID("CM_0011", HttpStatus.BAD_REQUEST),
  ENTITY_NOT_FOUND("CM_0012", HttpStatus.NOT_FOUND),
  INTERNAL_SERVER_ERROR("CM_0013", HttpStatus.INTERNAL_SERVER_ERROR),
  ACCESS_TOKEN_INVALID("CM_0014", HttpStatus.UNAUTHORIZED),
  ACCESS_TOKEN_EXPIRED("CM_0015", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_INVALID("CM_0016", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_EXPIRED("CM_0017", HttpStatus.UNAUTHORIZED),
  EXECUTE_THIRTY_SERVICE_ERROR("CM_0018", HttpStatus.INTERNAL_SERVER_ERROR),
  NOT_DECRYPT_SECURE_REQUEST("CM_0019", HttpStatus.BAD_REQUEST),
  NOT_ENCRYPT_SECURE_RESPONSE("CM_0020", HttpStatus.INTERNAL_SERVER_ERROR),
  SIGNATURE_INVALID("CM_0021", HttpStatus.UNAUTHORIZED),
  TOO_MANY_REQUESTS("CM_0022", HttpStatus.TOO_MANY_REQUESTS),
  MISSING_REQUEST_HEADER("CM_0023", HttpStatus.BAD_REQUEST),
  HTTP_MESSAGE_NOT_READABLE("CM_0024", HttpStatus.BAD_REQUEST),
  MISSING_REQUEST_PATH("CM_0025", HttpStatus.BAD_REQUEST),
  ;

  private final String errorCode;

  private final HttpStatus httpStatus;
}
