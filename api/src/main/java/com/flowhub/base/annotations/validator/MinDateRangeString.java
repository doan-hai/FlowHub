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
 * **Annotation `@MinDateRangeString`**
 *
 * <p>Annotation này đảm bảo rằng khoảng cách giữa hai thời điểm (`startField` và `endField`) không
 * nhỏ hơn giá trị `minDuration`.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MinDateRange(startField = "startDate", endField = "endDate", minDuration = 3, pattern = "dd-MM-yyyy", message = "Khoảng thời gian tối thiểu phải là 3 ngày!")
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
@Repeatable(MinDateRangeStringGroup.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MinDateRangeString.Validator.class)
public @interface MinDateRangeString {

  String message() default "{validation.range.date.min}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String startField();

  String endField();

  int minDuration() default 0;

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  @Slf4j
  class Validator extends AbstractDateRangeValidator<MinDateRangeString> {

    @Override
    protected String getStartField(MinDateRangeString annotation) {
      return annotation.startField();
    }

    @Override
    protected String getEndField(MinDateRangeString annotation) {
      return annotation.endField();
    }

    @Override
    protected long getDurationLimit(MinDateRangeString annotation) {
      return annotation.minDuration();
    }

    @Override
    protected boolean getAllowEqual(MinDateRangeString annotation) {
      return true;
    }

    @Override
    protected String getMessage(MinDateRangeString annotation) {
      return annotation.message();
    }

    @Override
    protected boolean isValidDuration(long durationBetween) {
      return durationBetween >= durationLimit;
    }

    @Override
    protected String getPattern(MinDateRangeString annotation) {
      return annotation.pattern();
    }
  }
}
