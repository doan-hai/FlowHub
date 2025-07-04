package com.flowhub.base.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * **Lớp `FeignLoggingConfiguration` - Cấu hình logging cho Feign Client**
 *
 * <p>Lớp này định nghĩa cấu hình logging cho tất cả các Feign Client trong hệ thống,
 * giúp theo dõi và ghi log chi tiết về các request và response.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi khi một Feign Client gửi hoặc nhận request, lớp này sẽ đảm bảo rằng
 * các thông tin quan trọng như HTTP method, URL, headers, và response body được ghi log một cách
 * chi tiết.</p>
 * <pre>
 * **📌 Ví dụ log request khi mức độ log là `BASIC`:**
 * ```
 * ---> GET https://api.example.com/users HTTP/1.1
 * ```
 *
 * **📌 Ví dụ log response khi mức độ log là `BASIC`:**
 * ```
 * <--- HTTP/1.1 200 OK (100ms)
 * ```
 *
 * **📌 Các mức độ log của Feign:**
 * - `NONE`    → Không ghi log request/response.
 * - `BASIC`   → Ghi log phương thức, URL, thời gian và mã trạng thái HTTP.
 * - `HEADERS` → Ghi log như `BASIC` + headers của request/response.
 * - `FULL`    → Ghi log như `HEADERS` + body request/response nếu có.
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
public class FeignLoggingConfiguration {

  /**
   * **Bean `FeignLogging` - Logger tùy chỉnh cho Feign**
   *
   * <p>Bean này cung cấp một instance của `FeignLogging`, giúp ghi log
   * chi tiết request và response của Feign Client.</p>
   *
   * @return Đối tượng `FeignLogging` để sử dụng trong Feign Client.
   */
  @Bean
  public FeignLogging customFeignRequestLogging() {
    return new FeignLogging();
  }

  /**
   * **Bean `Logger.Level` - Thiết lập mức độ log cho Feign**
   *
   * <p>Bean này thiết lập mức độ log cho Feign Client.
   * Mức độ mặc định được đặt là `BASIC`, giúp ghi log các thông tin quan trọng như phương thức
   * HTTP, URL, mã trạng thái và thời gian phản hồi.</p>
   *
   * @return Mức độ log `Logger.Level.BASIC` cho Feign Client.
   */
  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }
}