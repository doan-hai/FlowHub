package com.flowhub.base.annotations.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **Annotation `@MaxDateRangeStringGroup`**
 *
 * <p>Annotation này cho phép nhóm nhiều annotation `@MaxDateRangeString` trên cùng một class.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @MaxDateRangeStringGroup({
 *     @MaxDateRangeString(startField = "startDate", endField = "endDate", maxDuration = 30, message = "Khoảng thời gian không được vượt quá 30 ngày"),
 *     @MaxDateRangeString(startField = "registrationStart", endField = "registrationEnd", maxDuration = 7, message = "Khoảng thời gian đăng ký không được vượt quá 7 ngày")
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
public @interface MaxDateRangeStringGroup {

  MaxDateRangeString[] value();
}
