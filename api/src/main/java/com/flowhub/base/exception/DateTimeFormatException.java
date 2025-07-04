package com.flowhub.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * **Lớp `DateTimeFormatException` - Ngoại lệ định dạng ngày giờ không hợp lệ**
 *
 * <p>Lớp này mở rộng từ `RuntimeException` và được sử dụng để xử lý các trường hợp
 * định dạng ngày giờ không đúng khi nhập dữ liệu vào hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một giá trị ngày giờ không đúng định dạng được nhập vào hệ thống,
 * ngoại lệ `DateTimeFormatException` sẽ được ném ra để báo lỗi.</p>
 * <p>
 * **📌 Ví dụ sử dụng `DateTimeFormatException`:**
 * <pre>
 * {@code
 * public LocalDate parseDate(String dateStr) {
 *     try {
 *         return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
 *     } catch (DateTimeParseException e) {
 *         throw new DateTimeFormatException("Định dạng ngày không hợp lệ", "birthDate", dateStr);
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ phản hồi JSON khi lỗi xảy ra:**
 * <pre>
 * {
 *     "errorCode": "INVALID_DATE_FORMAT",
 *     "message": "Định dạng ngày không hợp lệ",
 *     "field": "birthDate",
 *     "value": "31-02-2023"
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class DateTimeFormatException extends RuntimeException {

  /** Thông điệp lỗi mô tả vấn đề định dạng ngày giờ** */
  private final String message;

  /** Tên trường dữ liệu bị lỗi** */
  private final String field;

  /** Giá trị sai định dạng gây ra lỗi** */
  private final String value;

  /**
   * **Constructor khởi tạo `DateTimeFormatException`**
   *
   * <p>Hàm khởi tạo nhận thông tin lỗi, bao gồm thông điệp lỗi (`message`),
   * tên trường bị lỗi (`field`) và giá trị sai định dạng (`value`).</p>
   *
   * @param message Thông điệp lỗi mô tả vấn đề.
   * @param field   Tên trường dữ liệu bị lỗi.
   * @param value   Giá trị sai định dạng gây lỗi.
   */
  public DateTimeFormatException(String message, String field, String value) {
    super(message);
    this.message = message;
    this.field = field;
    this.value = value;
  }
}