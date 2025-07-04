package com.flowhub.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;
import com.flowhub.base.data.SnowflakeGeneratorStrategy;

/**
 * Annotation dùng để đánh dấu một trường (field) là khóa chính được sinh tự động theo thuật toán
 * Snowflake.
 * <p>
 * 📌 Cách sử dụng:
 * <pre>
 * {@code
 * @SnowflakeGeneratedId
 * private Long id; }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@IdGeneratorType(SnowflakeGeneratorStrategy.class)
public @interface SnowflakeGeneratedId {

}
