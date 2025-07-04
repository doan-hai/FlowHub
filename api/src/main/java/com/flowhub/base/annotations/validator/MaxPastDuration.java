package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import lombok.extern.slf4j.Slf4j;
import com.flowhub.base.constant.DateConstant;
import com.flowhub.base.utils.DateUtils;
import com.flowhub.base.utils.MessageUtils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@MaxPastDuration`**
 *
 * <p>Annotation này dùng để kiểm tra xem khoảng thời gian giữa một ngày/thời gian đầu vào và thời
 * điểm hiện tại có vượt quá giá trị `maxDuration` hay không.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MaxPastDuration(maxDuration = 86400, pattern = "yyyy-MM-dd HH:mm:ss", message = "Ngày không được cách hiện tại quá 1 ngày!")
 * private String createdDate;
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MaxPastDuration.Validator.class)
public @interface MaxPastDuration {

  String message() default "{validation.date.max.past.duration}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int maxDuration(); // Khoảng cách tối đa

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  @Slf4j
  class Validator implements ConstraintValidator<MaxPastDuration, Object> {

    private int maxDuration;

    private String pattern;

    private String message;

    @Override
    public void initialize(MaxPastDuration annotation) {
      this.maxDuration = annotation.maxDuration();
      this.pattern = annotation.pattern();
      this.message = annotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }

      try {
        var inputTime = DateUtils.convertToTemporal(value, pattern);

        if (inputTime == null) {
          log.error("Invalid date/time format: {}", value);
          return true;
        }

        var now = getCurrentTemporal(inputTime);
        var durationBetween = ChronoUnit.SECONDS.between(now, inputTime);

        if (durationBetween > maxDuration) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate(
                     MessageUtils.resolveMessage(message, DateUtils.formatDuration(maxDuration)))
                 .addConstraintViolation();
          return false;
        }

      } catch (Exception e) {
        log.error("Validation error: {}", e.getMessage(), e);
      }
      return true;
    }

    private Temporal getCurrentTemporal(Temporal inputTime) {
      if (inputTime instanceof LocalDate) {
        return LocalDate.now();
      } else if (inputTime instanceof LocalTime) {
        return LocalTime.now();
      } else {
        return LocalDateTime.now();
      }
    }
  }
}
