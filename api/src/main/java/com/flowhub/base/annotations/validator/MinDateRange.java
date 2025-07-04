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
 * **Annotation `@MinDateRange`**
 *
 * <p>Annotation này đảm bảo rằng khoảng cách giữa hai thời điểm (`startField` và `endField`) không
 * nhỏ hơn giá trị `minDuration`.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MinDateRange(startField = "startDate", endField = "endDate", minDuration = 3, message = "Khoảng thời gian tối thiểu phải là 3 ngày!")
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
@Repeatable(MinDateRangeGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MinDateRange.Validator.class)
public @interface MinDateRange {

  String message() default "{validation.range.date.min}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  int minDuration() default 0; // Khoảng tối thiểu giữa 2 thời gian

  @Slf4j
  class Validator extends AbstractDateRangeValidator<MinDateRange> {

    @Override
    protected String getStartField(MinDateRange annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(MinDateRange annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(MinDateRange annotation) {
      return annotation.minDuration();
    }

    @Override
    protected boolean getAllowEqual(MinDateRange annotation) {
      return true;
    }

    @Override
    protected String getMessage(MinDateRange annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return durationBetween >= durationLimit;
    }

    @Override
    protected String getPattern(MinDateRange annotation) {
      return "";
    }
  }
}
