package com.flowhub.base.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import com.flowhub.base.utils.DateUtils;
import com.flowhub.base.utils.MessageUtils;

/**
 * AbstractDateRangeValidator là lớp cha dùng để kiểm tra mối quan hệ giữa 2 giá trị thời gian (ví
 * dụ: ngày bắt đầu và ngày kết thúc) được khai báo trong một annotation.
 *
 * <pre>
 * 📌 Chức năng chính:
 * - Xác định tên trường bắt đầu và kết thúc từ annotation.
 * - Chuyển đổi giá trị trường sang kiểu Temporal (LocalDate, LocalTime, LocalDateTime).
 * - Tính toán khoảng cách giữa hai giá trị và xác thực theo điều kiện cụ thể.
 * - Trả về thông báo lỗi khi điều kiện không được thoả mãn.
 * </pre>
 * <p>
 * Các lớp con cần triển khai logic cụ thể bằng cách override các phương thức trừu tượng như:
 * {@code getStartField}, {@code getEndField}, {@code isValidDuration}, v.v.
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
public abstract class AbstractDateRangeValidator<A extends Annotation>
    implements ConstraintValidator<A, Object> {

  /** Tên trường chứa giá trị thời gian bắt đầu. */
  protected String startFieldName;

  /** Tên trường chứa giá trị thời gian kết thúc. */
  protected String endFieldName;

  /** Giới hạn khoảng cách giữa hai thời điểm (tính bằng giây). */
  protected long durationLimit;

  /** Có cho phép giá trị bắt đầu bằng giá trị kết thúc hay không. */
  protected boolean allowEqual;

  /** Thông báo lỗi khi điều kiện không hợp lệ. */
  protected String message;

  /** Định dạng ngày giờ (dùng cho kiểu String). */
  protected String pattern;

  @Override
  public void initialize(A annotation) {
    this.startFieldName = getStartField(annotation);
    this.endFieldName = getEndField(annotation);
    this.durationLimit = getDurationLimit(annotation);
    this.allowEqual = getAllowEqual(annotation);
    this.message = getMessage(annotation);
    this.pattern = getPattern(annotation);
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    try {
      var startField = value.getClass().getDeclaredField(startFieldName);
      var endField = value.getClass().getDeclaredField(endFieldName);

      startField.setAccessible(true);
      endField.setAccessible(true);

      var startObj = startField.get(value);
      var endObj = endField.get(value);

      if (startObj == null || endObj == null) {
        return true;
      }

      var startTime = DateUtils.convertToTemporal(startObj, pattern);
      var endTime = DateUtils.convertToTemporal(endObj, pattern);

      if (startTime == null || endTime == null) {
        log.error("Fields {} and {} must be instances of LocalDate, LocalTime, or LocalDateTime",
                  startFieldName, endFieldName);
        return true;
      }

      var durationBetween =
          startTime instanceof LocalDate && endTime instanceof LocalDate ?
              ChronoUnit.DAYS.between(startTime, endTime) * 86400 :
              ChronoUnit.SECONDS.between(startTime, endTime);

      if (!isValidDuration(durationBetween)) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                   MessageUtils.resolveMessage(message,
                                               DateUtils.formatDuration(durationLimit)))
               .addPropertyNode(endFieldName)
               .addConstraintViolation();
        return false;
      }

    } catch (Exception e) {
      log.error("Error accessing fields {} and {}: {}",
                startFieldName,
                endFieldName,
                e.getMessage(),
                e);
    }
    return true;
  }

  protected abstract String getStartField(A annotation);

  protected abstract String getEndField(A annotation);

  protected abstract long getDurationLimit(A annotation);

  protected abstract boolean getAllowEqual(A annotation);

  protected abstract String getMessage(A annotation);

  protected abstract boolean isValidDuration(long durationBetween);

  protected abstract String getPattern(A annotation);
}
