package com.flowhub.base.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * **Lớp `BaseException` - Xử lý ngoại lệ tùy chỉnh trong hệ thống**
 *
 * <p>Lớp này mở rộng từ `RuntimeException` và được sử dụng để xử lý các ngoại lệ
 * trong hệ thống theo một cách chuẩn hóa, giúp dễ dàng quản lý và phân loại lỗi.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Ứng dụng có thể sử dụng `BaseException` để tạo và xử lý các ngoại lệ một cách đồng nhất.
 * Khi một lỗi xảy ra, `BaseException` sẽ chứa đầy đủ thông tin về lỗi, bao gồm mã lỗi, trạng thái
 * HTTP, thông điệp tùy chỉnh và nguyên nhân lỗi.</p>
 * <p>
 * **📌 Ví dụ sử dụng `BaseException`:**
 * <pre>
 * {@code
 * throw new BaseException(CommonError.NOT_FOUND, "Không tìm thấy dữ liệu");
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Getter
public class BaseException extends RuntimeException {

  /** Danh sách tham số động để format message lỗi** */
  private final Object[] messageArg;

  /** Đối tượng `AbstractError` chứa mã lỗi và trạng thái HTTP** */
  private final AbstractError abstractError;

  /** Nguyên nhân gốc của ngoại lệ** */
  private final Throwable cause;

  /** Thông điệp lỗi chi tiết** */
  private final String message;

  /**
   * **Constructor với `AbstractError`**
   *
   * <p>Sử dụng khi chỉ cần truyền đối tượng `AbstractError` mà không cần thông điệp tùy chỉnh.</p>
   *
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   */
  public BaseException(AbstractError abstractError) {
    this(null, abstractError, null, null);
  }

  /**
   * **Constructor với `AbstractError` và thông điệp lỗi**
   *
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   * @param message       Thông điệp lỗi tùy chỉnh.
   */
  public BaseException(AbstractError abstractError, String message) {
    this(null, abstractError, null, message);
  }

  /**
   * **Constructor với `AbstractError` và thông điệp lỗi**
   *
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   * @param cause         Nguyên nhân gốc của lỗi.
   */
  public BaseException(AbstractError abstractError, Throwable cause) {
    this(null, abstractError, cause, null);
  }

  /**
   * **Constructor với `AbstractError`, nguyên nhân lỗi (`cause`) và thông điệp lỗi**
   *
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   * @param cause         Nguyên nhân gốc của lỗi.
   * @param message       Thông điệp lỗi tùy chỉnh.
   */
  public BaseException(AbstractError abstractError, Throwable cause, String message) {
    this(null, abstractError, cause, message);
  }

  /**
   * **Constructor với tham số động (`messageArg`) và `AbstractError`**
   *
   * @param messageArg    Danh sách tham số để format thông điệp lỗi.
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   * @param message       Thông điệp lỗi tùy chỉnh.
   */
  public BaseException(Object[] messageArg, AbstractError abstractError, String message) {
    this(messageArg, abstractError, null, message);
  }

  /**
   * **Constructor đầy đủ với tất cả các thông tin lỗi**
   *
   * @param messageArg    Danh sách tham số để format thông điệp lỗi.
   * @param abstractError Đối tượng chứa mã lỗi và trạng thái HTTP.
   * @param cause         Nguyên nhân gốc của lỗi.
   * @param message       Thông điệp lỗi tùy chỉnh.
   */
  public BaseException(Object[] messageArg, AbstractError abstractError, Throwable cause,
                       String message) {
    super(message, cause);
    this.messageArg = messageArg;
    this.abstractError = abstractError;
    this.cause = cause;
    this.message = message;
  }

  /**
   * **Lấy mã lỗi từ `AbstractError`**
   *
   * <p>Phương thức này trả về mã lỗi của ngoại lệ, được lấy từ đối tượng `AbstractError`.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String errorCode = exception.getErrorCode(); // "404"
   * }
   * </pre>
   *
   * @return Mã lỗi dưới dạng chuỗi.
   */
  public String getErrorCode() {
    return abstractError.getErrorCode();
  }

  /**
   * **Lấy thông điệp lỗi (`getMessage()`)**
   *
   * <p>Nếu có `message`, phương thức sẽ format lại với `messageArg` nếu có.
   * Nếu không có `message`, phương thức sẽ lấy thông điệp từ nguyên nhân gốc (`cause`).</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String errorMessage = exception.getMessage();
   * System.out.println(errorMessage);
   * }
   * </pre>
   *
   * @return Thông điệp lỗi đầy đủ.
   */
  @Override
  public String getMessage() {
    return StringUtils.isNoneBlank(message) ?
        (messageArg == null
            ? message
            : String.format(message, messageArg)) : (cause != null ? cause.getMessage() : null);
  }

  /**
   * **Lấy thông điệp lỗi dạng đầy đủ (`getLocalizedMessage()`)**
   *
   * <p>Phương thức này trả về một chuỗi chứa tên lớp ngoại lệ, mã lỗi,
   * thông điệp lỗi và trạng thái HTTP.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String localizedMessage = exception.getLocalizedMessage();
   * System.out.println(localizedMessage);
   * // Output: BaseException[404-Không tìm thấy dữ liệu-HttpStatus.NOT_FOUND]
   * }
   * </pre>
   *
   * @return Chuỗi chứa thông tin lỗi đầy đủ.
   */
  @Override
  public String getLocalizedMessage() {
    String message = getMessage();
    if (StringUtils.isNoneBlank(message)) {
      return BaseException.class.getSimpleName()
          + "["
          + abstractError.getErrorCode()
          + "-"
          + message
          + "-"
          + abstractError.getHttpStatus()
          + "]";
    }
    return BaseException.class.getSimpleName()
        + "["
        + abstractError.getErrorCode()
        + "-"
        + abstractError
        + "-"
        + abstractError.getHttpStatus()
        + "]";
  }

  /**
   * **Lấy trạng thái HTTP của ngoại lệ**
   *
   * <p>Phương thức này trả về trạng thái HTTP tương ứng với lỗi,
   * giúp API phản hồi đúng theo chuẩn HTTP.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * HttpStatus status = exception.getHttpStatus();
   * System.out.println(status); // HttpStatus.NOT_FOUND
   * }
   * </pre>
   *
   * @return Trạng thái HTTP của lỗi.
   */
  public HttpStatus getHttpStatus() {
    return abstractError.getHttpStatus();
  }
}