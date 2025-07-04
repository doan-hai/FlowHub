package com.flowhub.base.config;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 * **Tùy chỉnh RestTemplate (`CustomRestTemplateCustomizer`)**
 *
 * <p>Lớp này được sử dụng để tùy chỉnh đối tượng `RestTemplate` trong ứng dụng.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Cấu hình `BufferingClientHttpRequestFactory` để hỗ trợ đọc lại request body nhiều lần.</li>
 *   <li>✅ Thêm `CustomClientHttpRequestInterceptor` vào danh sách các interceptor của `RestTemplate`.</li>
 *   <li>✅ Hỗ trợ ghi log chi tiết yêu cầu (request) và phản hồi (response) khi sử dụng `RestTemplate`.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Lớp này triển khai giao diện `RestTemplateCustomizer`, cung cấp phương thức `customize`
 * để cấu hình `RestTemplate` trước khi sử dụng.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong cấu hình `RestTemplate`:**
 * <pre>
 * {@code
 * @Bean
 * public RestTemplate restTemplate(RestTemplateBuilder builder, CustomRestTemplateCustomizer customizer) {
 *     RestTemplate restTemplate = builder.build();
 *     customizer.customize(restTemplate);
 *     return restTemplate;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public class CustomRestTemplateCustomizer implements RestTemplateCustomizer {

  /**
   * **Tùy chỉnh đối tượng `RestTemplate`**
   *
   * <p>Phương thức này được gọi tự động khi Spring Boot khởi tạo `RestTemplate`. Nó thực hiện các
   * nhiệm vụ sau:</p>
   * <ul>
   *   <li>📌 Thiết lập `BufferingClientHttpRequestFactory` để cho phép đọc request body nhiều lần.</li>
   *   <li>📌 Thêm `CustomClientHttpRequestInterceptor` để ghi log chi tiết các yêu cầu và phản hồi HTTP.</li>
   * </ul>
   *
   * @param restTemplate Đối tượng `RestTemplate` cần được tùy chỉnh.
   */
  @Override
  public void customize(RestTemplate restTemplate) {
    // Thiết lập BufferingClientHttpRequestFactory để có thể đọc lại nội dung request nhiều lần
    restTemplate.setRequestFactory(
        new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
    // Thêm interceptor tùy chỉnh để ghi log yêu cầu và phản hồi HTTP
    restTemplate.getInterceptors().add(new CustomClientHttpRequestInterceptor());
  }
}
