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
 * **Annotation `@FutureDateString`**
 *
 * <p>Annotation này dùng để kiểm tra xem một chuỗi ngày/thời gian có nằm trong tương lai hay
 * không.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @FutureDateString(pattern = "yyyy-MM-dd HH:mm:ss", message = "Thời gian phải trong tương lai!")
 * private String eventTime;
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
@Constraint(validatedBy = FutureDateString.Validator.class)
public @interface FutureDateString {

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  String message() default "{validation.date.future}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Slf4j
  class Validator implements ConstraintValidator<FutureDateString, String> {

    private String pattern;

    @Override
    public void initialize(FutureDateString annotation) {
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
          return date.isAfter(LocalDateTime.now());
        } else if (formatType == 1) {
          var date = LocalDate.parse(value, DateUtils.getConfigFormatter().get(pattern));
          return date.isAfter(LocalDate.now());
        } else if (formatType == 2) {
          var date = LocalTime.parse(value, DateUtils.getConfigFormatter().get(pattern));
          return date.isAfter(LocalTime.now());
        }
      } catch (DateTimeParseException e) {
        log.error("Invalid date format: {}", value);
      }
      return true;
    }
  }
}
