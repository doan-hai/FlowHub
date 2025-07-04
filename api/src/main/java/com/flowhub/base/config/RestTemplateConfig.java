package com.flowhub.base.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

/**
 * **Cấu hình `RestTemplate` (`RestTemplateConfig`)**
 *
 * <p>Lớp này chịu trách nhiệm cấu hình `RestTemplate` - một công cụ mạnh mẽ trong Spring
 * giúp gửi HTTP request đến các API bên ngoài.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Cung cấp một `RestTemplate` để gọi API bên ngoài.</li>
 *   <li>✅ Sử dụng `RestTemplateBuilder` để tùy chỉnh `RestTemplate`.</li>
 *   <li>✅ Sử dụng `CustomRestTemplateCustomizer` để thêm các cấu hình tùy chỉnh (interceptor, timeout,...).</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>`RestTemplate` có thể được tùy chỉnh thông qua `RestTemplateBuilder`, cho phép thiết lập các
 * giá trị như timeout, interceptors, error handlers, v.v.</p>
 * <p>
 * **📌 Ví dụ sử dụng `RestTemplate`:**
 * <pre>
 * {@code
 * @Autowired
 * private RestTemplate restTemplate;
 *
 * public String getExternalData() {
 *     String response = restTemplate.getForObject("https://api.example.com/data", String.class);
 *     return response;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
public class RestTemplateConfig {

  /**
   * **Tạo một bean `CustomRestTemplateCustomizer`**
   *
   * <p>Phương thức này khởi tạo một `CustomRestTemplateCustomizer` để
   * cấu hình các tùy chỉnh bổ sung cho `RestTemplate`.</p>
   *
   * @return Đối tượng `CustomRestTemplateCustomizer`.
   */
  @Bean
  public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
    return new CustomRestTemplateCustomizer();
  }

  /**
   * **Tạo `RestTemplateBuilder` với tùy chỉnh từ `CustomRestTemplateCustomizer`**
   *
   * <p>Phương thức này sử dụng `CustomRestTemplateCustomizer` để tùy chỉnh
   * `RestTemplateBuilder`.</p>
   * <p>
   * **Lưu ý:** Sử dụng `@DependsOn("customRestTemplateCustomizer")` để đảm bảo rằng
   * `CustomRestTemplateCustomizer` được khởi tạo trước khi `RestTemplateBuilder`.</p>
   *
   * @return Đối tượng `RestTemplateBuilder` đã được cấu hình.
   */
  @Bean
  @DependsOn(value = {"customRestTemplateCustomizer"})
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder(customRestTemplateCustomizer());
  }

  /**
   * **Tạo `RestTemplate` từ `RestTemplateBuilder`**
   *
   * <p>Phương thức này tạo một `RestTemplate` sử dụng `RestTemplateBuilder`,
   * đảm bảo `RestTemplate` có thể được tùy chỉnh dễ dàng.</p>
   *
   * @param builder Đối tượng `RestTemplateBuilder` để tạo `RestTemplate`.
   * @return Đối tượng `RestTemplate` đã được cấu hình.
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
