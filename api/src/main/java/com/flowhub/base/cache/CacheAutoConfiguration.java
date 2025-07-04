package com.flowhub.base.cache;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * **Cấu hình tự động cho bộ nhớ đệm (`CacheAutoConfiguration`)**
 *
 * <p>Lớp này cấu hình tự động cho các thuộc tính của bộ nhớ đệm trong ứng dụng. Nó cho phép
 * sử dụng các thuộc tính được định nghĩa trong lớp `CacheProperties`.</p>
 *
 * <h2>📌 Chức năng chính:</h2>
 * <ul>
 *   <li>✅ Kích hoạt lớp `CacheProperties` để tự động ánh xạ các giá trị từ file cấu hình.</li>
 *   <li>✅ Sử dụng annotation `@Configuration` để đánh dấu đây là một lớp cấu hình.</li>
 *   <li>✅ Không chứa phương thức khai báo bean, chỉ đơn thuần cung cấp cấu hình cho hệ thống.</li>
 * </ul>
 *
 * <h2>📌 Cách hoạt động:</h2>
 * <p>Lớp này sử dụng các annotation quan trọng sau:</p>
 * <ul>
 *   <li><b>`@Configuration`</b>: Xác định rằng đây là một lớp cấu hình trong Spring Boot.</li>
 *   <li><b>`proxyBeanMethods = false`</b>: Tối ưu hóa hiệu suất bằng cách không sử dụng proxy CGLIB,
 *   cho phép gọi trực tiếp các phương thức cấu hình mà không cần tạo proxy cho mỗi lần gọi.</li>
 *   <li><b>`@EnableConfigurationProperties(CacheProperties.class)`</b>: Kích hoạt hỗ trợ cho `@ConfigurationProperties`
 *   trong `CacheProperties`, giúp ánh xạ các thuộc tính từ file cấu hình (`application.yml` hoặc `application.properties`).</li>
 * </ul>
 *
 * <h2>📌 Ví dụ file cấu hình:</h2>
 * <p>Giả sử bạn muốn sử dụng Caffeine làm bộ nhớ đệm, bạn có thể cấu hình trong `application.yml` như sau:</p>
 * <pre>
 * {@code
 * custom:
 *   properties:
 *     cache:
 *       type: caffeine
 *       properties:
 *         userCache:
 *           expiredTime: 600
 *           maximumSize: 1000
 * }
 * </pre>
 *
 * <h2>📌 Tại sao cần `CacheAutoConfiguration`?</h2>
 * <p>Lớp này giúp tự động tải và sử dụng các thuộc tính bộ nhớ đệm mà không cần phải khai báo thủ công.
 * Nhờ đó, bạn có thể dễ dàng thay đổi loại bộ nhớ đệm hoặc cấu hình cache mà không cần sửa đổi mã nguồn.</p>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

}
