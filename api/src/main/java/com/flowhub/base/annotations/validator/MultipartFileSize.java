package com.flowhub.base.annotations.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.web.multipart.MultipartFile;
import com.flowhub.base.utils.MessageUtils;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * **Annotation `@MultipartFileSize`**
 *
 * <p>Annotation này được sử dụng để kiểm tra kích thước của tệp tải lên (upload file).</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * public class FileUploadRequest {
 *     @MultipartFileSize(maxSize = 1048576, message = "File size must be less than 1MB")
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
@Constraint(validatedBy = MultipartFileSize.Validator.class)
public @interface MultipartFileSize {

  long maxSize() default Long.MAX_VALUE;

  String message() default "{validation.file.size}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<MultipartFileSize, MultipartFile> {

    private MultipartFileSize annotation;

    @Override
    public void initialize(MultipartFileSize annotation) {
      this.annotation = annotation;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
      if (file == null || file.isEmpty()) {
        return true;
      }

      var fileSize = file.getSize();
      if (fileSize > annotation.maxSize()) {
        var messageTemplate = MessageUtils.resolveMessage(annotation.message(),
                                                          annotation.maxSize());
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate)
               .addConstraintViolation();
        return false;
      }
      return true;
    }
  }
}
