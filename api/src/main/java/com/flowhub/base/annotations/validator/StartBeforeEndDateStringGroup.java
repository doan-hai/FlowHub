package com.flowhub.base.annotations.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **Annotation `@StartBeforeEndDateStringGroup`**
 *
 * <p>Annotation này cho phép nhóm nhiều annotation `@StartBeforeEndDateString` trên cùng một
 * class.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * @StartBeforeEndDateStringGroup({
 *     @StartBeforeEndDateString(startField = "startDate", endField = "endDate", message = "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc"),
 *     @StartBeforeEndDateString(startField = "registrationStart", endField = "registrationEnd", message = "Ngày bắt đầu đăng ký phải nhỏ hơn ngày kết thúc đăng ký")
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
public @interface StartBeforeEndDateStringGroup {

  StartBeforeEndDateString[] value();
}
