package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.flowhub.base.constant.RegexConstant;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@Alphanumeric`**
 *
 * <p>Dùng để kiểm tra xem một chuỗi có chỉ chứa ký tự chữ và số hay không.</p>
 * <pre>
 * - **Không cho phép ký tự đặc biệt hoặc khoảng trắng.**
 * - **Hỗ trợ validation trên các thuộc tính của entity, DTO, và method parameters.**
 * - **Dựa trên regex được định nghĩa trong `RegexConstant.ALPHANUMERIC_WITHOUT_SPACE`**</pre>
 * <p>
 * 📌 **Ví dụ sử dụng:**
 * <pre>
 * {@code
 * public class User {
 *     @Alphanumeric(message = "Username must contain only letters and numbers")
 *     private String username;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = Alphanumeric.Validator.class)
public @interface Alphanumeric {

  String message() default "{validation.alphanumeric}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<Alphanumeric, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null || value.isEmpty()) {
        return true;
      }
      return value.matches(RegexConstant.ALPHANUMERIC_WITHOUT_SPACE);
    }
  }
}
