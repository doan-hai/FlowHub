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
import com.flowhub.base.constant.DateConstant;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@StartBeforeEndDateString`**
 *
 * <p>Annotation này được sử dụng để kiểm tra xem một giá trị ngày/thời gian bắt đầu dưới dạng
 * chuỗi (`String`) có nhỏ hơn hoặc bằng giá trị ngày/thời gian kết thúc hay không.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @StartBeforeEndDateString(
 *     startField = "startDate",
 *     endField = "endDate",
 *     allowEqualDates = true,
 *     pattern = "yyyy-MM-dd",
 *     message = "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc"
 * )
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
@Repeatable(StartBeforeEndDateStringGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StartBeforeEndDateString.Validator.class)
public @interface StartBeforeEndDateString {

  String message() default "{validation.range.date.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  boolean allowEqualDates() default true; // Cho phép ngày bắt đầu bằng ngày kết thúc

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  @Slf4j
  class Validator extends AbstractDateRangeValidator<StartBeforeEndDateString> {

    @Override
    protected String getStartField(StartBeforeEndDateString annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(StartBeforeEndDateString annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(StartBeforeEndDateString annotation) {
      return 0;
    }

    @Override
    protected boolean getAllowEqual(StartBeforeEndDateString annotation) {
      return annotation.allowEqualDates();
    }

    @Override
    protected String getMessage(StartBeforeEndDateString annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return allowEqual ? durationBetween >= 0 : durationBetween > 0;
    }

    @Override
    protected String getPattern(StartBeforeEndDateString annotation) {
      return annotation.pattern();
    }
  }
}
