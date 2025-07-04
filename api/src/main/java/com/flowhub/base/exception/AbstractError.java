package com.flowhub.base.exception;

import org.springframework.http.HttpStatus;

/**
 * **Giao diện `AbstractError` - Chuẩn hóa lỗi trong hệ thống**
 *
 * <p>Giao diện này định nghĩa cấu trúc chung cho các mã lỗi và trạng thái HTTP
 * tương ứng trong hệ thống, giúp chuẩn hóa cách xử lý lỗi.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi lỗi trong hệ thống sẽ có một mã lỗi (`errorCode`) và một mã trạng thái HTTP
 * (`httpStatus`). Khi một ngoại lệ xảy ra, hệ thống có thể sử dụng giao diện này để ánh xạ lỗi tới
 * phản hồi API phù hợp.</p>
 * <p>
 * **📌 Ví dụ triển khai `AbstractError` trong một `enum`:**
 * <pre>
 * {@code
 * public enum CommonError implements AbstractError {
 *     BAD_REQUEST("400", HttpStatus.BAD_REQUEST),
 *     NOT_FOUND("404", HttpStatus.NOT_FOUND);
 *
 *     private final String errorCode;
 *     private final HttpStatus httpStatus;
 *
 *     CommonError(String errorCode, HttpStatus httpStatus) {
 *         this.errorCode = errorCode;
 *         this.httpStatus = httpStatus;
 *     }
 *
 *     @Override
 *     public String getErrorCode() {
 *         return errorCode;
 *     }
 *
 *     @Override
 *     public HttpStatus getHttpStatus() {
 *         return httpStatus;
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ứng dụng thực tế:**
 * <ul>
 *   <li>📌 Chuẩn hóa các lỗi trong hệ thống.</li>
 *   <li>📌 Dễ dàng mở rộng danh sách lỗi bằng cách tạo thêm enum hoặc class triển khai `AbstractError`.</li>
 *   <li>📌 Hỗ trợ trả về phản hồi API đồng nhất dựa trên mã lỗi và trạng thái HTTP.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
public interface AbstractError {

  /**
   * **Lấy mã lỗi (`getErrorCode`)**
   *
   * <p>Phương thức này trả về mã lỗi duy nhất, giúp hệ thống dễ dàng nhận diện lỗi.</p>
   * <p>
   * **📌 Ví dụ trả về mã lỗi:**
   * <pre>
   * {@code
   * String errorCode = CommonError.BAD_REQUEST.getErrorCode(); // "400"
   * }
   * </pre>
   *
   * @return Mã lỗi dưới dạng chuỗi.
   */
  String getErrorCode();

  /**
   * **Lấy trạng thái HTTP tương ứng với lỗi (`getHttpStatus`)**
   *
   * <p>Phương thức này giúp xác định mã trạng thái HTTP của lỗi,
   * giúp API phản hồi đúng theo tiêu chuẩn HTTP.</p>
   * <p>
   * **📌 Ví dụ trả về mã trạng thái HTTP:**
   * <pre>
   * {@code
   * HttpStatus status = CommonError.NOT_FOUND.getHttpStatus(); // HttpStatus.NOT_FOUND (404)
   * }
   * </pre>
   *
   * @return Mã trạng thái HTTP tương ứng với lỗi.
   */
  HttpStatus getHttpStatus();
}