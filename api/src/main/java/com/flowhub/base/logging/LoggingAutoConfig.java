package com.flowhub.base.logging;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * **Lớp `LoggingAutoConfig` - Cấu hình tự động cho Logging**
 *
 * <p>Lớp này chịu trách nhiệm cấu hình tự động cho các thuộc tính logging
 * trong ứng dụng Spring Boot, giúp dễ dàng tùy chỉnh thông qua `LoggingProperties`.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Lớp này sử dụng annotation `@EnableConfigurationProperties(LoggingProperties.class)`,
 * giúp Spring Boot tự động nạp các thuộc tính logging từ file cấu hình.</p>
 * <p>
 * **📌 Ví dụ cấu hình trong `application.yml`:**
 * <pre>
 * {@code
 * logging:
 *   level: INFO
 *   enable-request-logging: true
 *   enable-response-logging: false
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAutoConfig {

}