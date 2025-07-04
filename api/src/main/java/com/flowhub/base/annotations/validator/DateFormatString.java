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
 * **Annotation `@DateFormatString`**
 *
 * <p>Annotation này được sử dụng để kiểm tra xem chuỗi đầu vào có đúng định dạng ngày hợp lệ hay
 * không.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @DateFormatString(pattern = "yyyy-MM-dd", message = "Ngày không hợp lệ!")
 * private String birthDate;
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
@Constraint(validatedBy = DateFormatString.Validator.class)
public @interface DateFormatString {

  String pattern() default DateConstant.DD_MM_YYYY_DASH;

  String message() default "{validation.date.invalidFormat}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Slf4j
  class Validator implements ConstraintValidator<DateFormatString, String> {

    private String pattern;

    @Override
    public void initialize(DateFormatString annotation) {
      this.pattern = annotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null || value.trim().isEmpty()) {
        return true;
      }
      try {
        var formatType = DateUtils.getFormatType(pattern);
        // Xác định kiểu dữ liệu và thực hiện so sánh với thời gian hiện tại
        if (formatType == 0) {
          LocalDateTime.parse(value, DateUtils.getConfigFormatter().get(pattern));
        } else if (formatType == 1) {
          LocalDate.parse(value, DateUtils.getConfigFormatter().get(pattern));
        } else if (formatType == 2) {
          LocalTime.parse(value, DateUtils.getConfigFormatter().get(pattern));
        }
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }
  }
}
