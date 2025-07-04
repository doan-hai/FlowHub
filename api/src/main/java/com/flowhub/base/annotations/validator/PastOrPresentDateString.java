package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import com.flowhub.base.constant.DateConstant;
import com.flowhub.base.utils.DateUtils;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@PastOrPresentDateString`**
 *
 * <p>Annotation này được sử dụng để kiểm tra xem một chuỗi ngày/thời gian có nằm trong quá khứ
 * hoặc là thời điểm hiện tại không.</p>
 *
 * <pre>
 * 📌 Cách hoạt động:
 * 1. Nhận giá trị đầu vào (`String value`).
 * 2. Chuyển đổi giá trị thành `Temporal` dựa trên `pattern` đã chỉ định.
 * 3. Xác định kiểu dữ liệu (`LocalDate`, `LocalTime`, `LocalDateTime`) thông qua `getFormatType()`.
 * 4. So sánh giá trị với thời gian hiện tại để kiểm tra hợp lệ.
 * </pre>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @PastOrPresentDateString(pattern = "yyyy-MM-dd HH:mm:ss", message = "Thời gian phải là quá khứ hoặc hiện tại!")
 * private String createdAt;
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({
    METHOD,
    FIELD,
    ANNOTATION_TYPE,
    CONSTRUCTOR,
    PARAMETER,
})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PastOrPresentDateString.Validator.class)
public @interface PastOrPresentDateString {

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  String message() default "{validation.date.past}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Slf4j
  class Validator implements ConstraintValidator<PastOrPresentDateString, String> {

    private String pattern;

    @Override
    public void initialize(PastOrPresentDateString annotation) {
      this.pattern = annotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null || value.trim().isEmpty()) {
        return true;
      }
      try {
        var formatType = DateUtils.getFormatType(pattern);
        if (formatType == 0) {
          var date = LocalDateTime.parse(value, DateUtils.getConfigFormatter().get(pattern));
          return !date.isAfter(LocalDateTime.now());
        } else if (formatType == 1) {
          var date = LocalDate.parse(value, DateUtils.getConfigFormatter().get(pattern));
          return !date.isAfter(LocalDate.now());
        } else if (formatType == 2) {
          var date = LocalTime.parse(value, DateUtils.getConfigFormatter().get(pattern));
          return !date.isAfter(LocalTime.now());
        }
      } catch (DateTimeParseException e) {
        log.error("Invalid date format: {}", value);
      }
      return true;
    }
  }
}
