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
 * **Annotation `@MaxDateRangeString`**
 *
 * <p>Annotation này đảm bảo rằng khoảng cách giữa hai giá trị ngày/thời gian (`startField` và
 * `endField`) không vượt quá giới hạn `minDuration`.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MaxDateRangeString(startField = "startDate", endField = "endDate", minDuration = 7, pattern = "dd-MM-yyyy", message = "Khoảng cách giữa hai ngày không được vượt quá 7 ngày!")
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
@Repeatable(MaxDateRangeStringGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MaxDateRangeString.Validator.class)
public @interface MaxDateRangeString {

  String message() default "{validation.range.date.max}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  int maxDuration() default 0;

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  @Slf4j
  class Validator extends AbstractDateRangeValidator<MaxDateRangeString> {

    @Override
    protected String getStartField(MaxDateRangeString annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(MaxDateRangeString annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(MaxDateRangeString annotation) {
      return annotation.maxDuration();
    }

    @Override
    protected boolean getAllowEqual(MaxDateRangeString annotation) {
      return true;
    }

    @Override
    protected String getMessage(MaxDateRangeString annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return durationBetween <= durationLimit;
    }

    @Override
    protected String getPattern(MaxDateRangeString annotation) {
      return annotation.pattern();
    }
  }
}
