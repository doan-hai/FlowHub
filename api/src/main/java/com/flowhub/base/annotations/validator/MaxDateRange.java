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
 * **Annotation `@MaxDateRange`**
 *
 * <p>Annotation này đảm bảo rằng khoảng cách giữa hai thời điểm (`startField` và `endField`) không
 * vượt quá giá trị `maxDuration`.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MaxDateRange(startField = "startDate", endField = "endDate", maxDuration = 7, message = "Khoảng thời gian không được vượt quá 7 ngày!")
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
@Repeatable(MaxDateRangeGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MaxDateRange.Validator.class)
public @interface MaxDateRange {

  String message() default "{validation.range.date.max}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  int maxDuration() default Integer.MAX_VALUE; // Khoảng tối đa giữa 2 thời gian

  @Slf4j
  class Validator extends AbstractDateRangeValidator<MaxDateRange> {

    @Override
    protected String getStartField(MaxDateRange annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(MaxDateRange annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(MaxDateRange annotation) {
      return annotation.maxDuration();
    }

    @Override
    protected boolean getAllowEqual(MaxDateRange annotation) {
      return true;
    }

    @Override
    protected String getMessage(MaxDateRange annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return durationBetween <= durationLimit;
    }

    @Override
    protected String getPattern(MaxDateRange annotation) {
      return "";
    }
  }
}
