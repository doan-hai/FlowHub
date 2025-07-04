package com.flowhub.base.annotations.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **Annotation `@MinDateRangeGroup`**
 *
 * <p>Annotation này cho phép nhóm nhiều annotation `@MinDateRange` trên cùng một class.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MinDateRangeGroup({
 *     @MinDateRange(startField = "startDate", endField = "endDate", minDuration = 3, message = "Khoảng thời gian tối thiểu là 3 ngày"),
 *     @MinDateRange(startField = "registrationStart", endField = "registrationEnd", minDuration = 7, message = "Thời gian đăng ký tối thiểu là 7 ngày")
 * })
 * public class BookingRequest {
 *     private String startDate;
 *     private String endDate;
 *     private String registrationStart;
 *     private String registrationEnd;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface MinDateRangeGroup {

  MinDateRange[] value();
}
