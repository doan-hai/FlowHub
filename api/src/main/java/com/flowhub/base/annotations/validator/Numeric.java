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
 * **Annotation `@Numeric`**
 *
 * <p>Annotation này được sử dụng để kiểm tra xem một chuỗi có phải là số hay không.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * public class Product {
 *     @Numeric(message = "Price must be a valid number")
 *     private String price;
 * }
 * }
 * </pre>
 * <p>
 * 📌 **Trong Controller:**
 * <pre>
 * {@code
 * @PostMapping
 * public ResponseEntity<String> validateNumber(@Valid @RequestParam("value") @Numeric String value) {
 *     return ResponseEntity.ok("Valid numeric input!");
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
@Constraint(validatedBy = Numeric.Validator.class)
public @interface Numeric {

  String message() default "{validation.numeric}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<Numeric, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null || value.isEmpty()) {
        return true;
      }
      return value.matches(RegexConstant.NUMERIC_REGEX);
    }
  }
}
