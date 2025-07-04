package com.flowhub.base.annotations.deserializer;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **Annotation `@DateJsonDeserialize`**
 *
 * <p>Annotation này được sử dụng để xác định cách deserialization một giá trị ngày/thời gian từ
 * JSON.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * public class BookingRequest {
 *
 *     @DateJsonDeserialize(type = DateJsonDeserialize.Type.DATETIME, message = "Ngày giờ không hợp lệ")
 *     private String bookingDate;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = TemporalDeserializer.class)
public @interface DateJsonDeserialize {

  String message() default "{validation.datetime.format}";

  // Xác định kiểu dữ liệu thời gian mà chuỗi JSON sẽ được chuyển đổi thành.
  Type type() default Type.DATE;

  enum Type {
    DATE,          // Chuyển đổi thành LocalDate
    TIME,          // Chuyển đổi thành LocalTime
    DATETIME,      // Chuyển đổi thành LocalDateTime
    START_OF_DAY,  // Chuyển đổi thành LocalDateTime với 00:00:00
    END_OF_DAY     // Chuyển đổi thành LocalDateTime với 23:59:59.999999999
  }
}
