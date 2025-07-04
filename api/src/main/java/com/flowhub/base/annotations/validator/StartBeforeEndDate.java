package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lombok.extern.slf4j.Slf4j;
import com.flowhub.base.annotations.AbstractDateRangeValidator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@StartBeforeEndDate`**
 *
 * <p>Annotation này được sử dụng để kiểm tra xem một giá trị ngày/thời gian bắt đầu có nhỏ hơn
 * hoặc bằng giá trị ngày/thời gian kết thúc hay không.</p>
 *
 * <pre>
 * 📌 Cách hoạt động:
 * 1. Nhận giá trị từ `startField` và `endField`.
 * 2. Chuyển đổi giá trị thành `Temporal` (nếu là `String` thì parse theo format quy định).
 * 3. So sánh giá trị bắt đầu với giá trị kết thúc.
 * 4. Nếu giá trị bắt đầu lớn hơn giá trị kết thúc (và `allowEqualDates` là `false`), báo lỗi validation.
 * </pre>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @StartBeforeEndDate(startField = "startDate", endField = "endDate", message = "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc")
 * public class BookingRequest {
 *     private String startDate;
 *     private String endDate;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Repeatable(StartBeforeEndDateGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StartBeforeEndDate.Validator.class)
public @interface StartBeforeEndDate {

  String message() default "{validation.range.date.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  boolean allowEqualDates() default true; // Cho phép ngày bắt đầu bằng ngày kết thúc

  @Slf4j
  class Validator extends AbstractDateRangeValidator<StartBeforeEndDate> {

    @Override
    protected String getStartField(StartBeforeEndDate annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(StartBeforeEndDate annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(StartBeforeEndDate annotation) {
      return 0;
    }

    @Override
    protected boolean getAllowEqual(StartBeforeEndDate annotation) {
      return annotation.allowEqualDates();
    }

    @Override
    protected String getMessage(StartBeforeEndDate annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return allowEqual ? durationBetween >= 0 : durationBetween > 0;
    }

    @Override
    protected String getPattern(StartBeforeEndDate annotation) {
      return "";
    }
  }
}
