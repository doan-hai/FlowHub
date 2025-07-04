package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.springframework.web.multipart.MultipartFile;
import com.flowhub.base.utils.MessageUtils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@MultipartFileType`**
 *
 * <p>Annotation này được sử dụng để kiểm tra **loại MIME (file type)** của tệp tải lên.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * public class FileUploadRequest {
 *     @MultipartFileType(allowedTypes = {"image/png", "image/jpeg"},
 *                        message = "Only PNG and JPEG files are allowed")
 *     private MultipartFile file;
 * }
 * }
 * </pre>
 * <p>
 * 📌 **Trong Controller:**
 * <pre>
 * {@code
 * @PostMapping
 * public ResponseEntity<String> uploadFile(@Valid @RequestParam("file") MultipartFile file) {
 *     return ResponseEntity.ok("File uploaded successfully!");
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = MultipartFileType.Validator.class)
public @interface MultipartFileType {

  String[] allowedTypes() default {};

  String message() default "{validation.file.type}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<MultipartFileType, MultipartFile> {

    private MultipartFileType annotation;

    @Override
    public void initialize(MultipartFileType annotation) {
      this.annotation = annotation;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
      if (file == null || file.isEmpty()) {
        return true;
      }
      if (annotation.allowedTypes().length > 0
          && Arrays.asList(annotation.allowedTypes()).contains(file.getContentType())) {
        return true;
      }
      var messageTemplate = MessageUtils.resolveMessage(annotation.message(),
                                                        String.join(", ",
                                                                    annotation.allowedTypes()));
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(messageTemplate)
             .addConstraintViolation();
      return false;
    }
  }
}
