package com.flowhub.base.logging;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.flowhub.base.constant.RequestConstant;

/**
 * **Lớp `LoggingProperties` - Cấu hình Logging cho hệ thống**
 *
 * <p>Lớp này định nghĩa các thuộc tính cấu hình cho việc ghi log request và response
 * trong hệ thống, giúp kiểm soát dữ liệu được ghi lại để giám sát và debug.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các thuộc tính trong lớp này sẽ được ánh xạ từ file cấu hình `application.yml`
 * hoặc `application.properties` với prefix `custom.properties.logging`.</p>
 * <p>
 * **📌 Ví dụ cấu hình trong `application.yml`:**
 * <pre>
 * {@code
 * custom:
 *   properties:
 *     logging:
 *       requestMaxPayloadLength: 5000
 *       responseMaxPayloadLength: 2000
 *       defaultIgnoreLogUri: true
 *       ignoreLogUri:
 *         - "/health"
 *         - "/metrics"
 *       excludeResponseBody: false
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@ConfigurationProperties(prefix = "custom.properties.logging")
public class LoggingProperties {

  /** Giới hạn tối đa kích thước payload của request khi ghi log (mặc định: 10000 bytes)** */
  @Getter
  private int requestMaxPayloadLength = 10000;

  /** Giới hạn tối đa kích thước payload của response khi ghi log (mặc định: 1000 bytes)** */
  @Getter
  private int responseMaxPayloadLength = 1000;

  /** Bật/tắt danh sách URI mặc định cần bỏ qua khi ghi log (mặc định: true)** */
  @Getter
  private boolean defaultIgnoreLogUri = true;

  /** Danh sách URI không cần ghi log, có thể được cấu hình trong file application.yml** */
  private Set<String> ignoreLogUri = new HashSet<>();

  /** Bật/tắt việc loại trừ response body khỏi log (mặc định: true)** */
  @Getter
  private boolean excludeResponseBody = true;

  /**
   * **Lấy danh sách URI cần bỏ qua khi ghi log**
   *
   * <p>Phương thức này kiểm tra xem có sử dụng danh sách URI mặc định hay không.
   * Nếu `defaultIgnoreLogUri` được bật (`true`), hệ thống sẽ thêm danh sách mặc định từ
   * `RequestConstant.WHITE_LIST_REQUEST` vào danh sách `ignoreLogUri`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Nếu `defaultIgnoreLogUri = true`: Lấy danh sách mặc định từ `RequestConstant.WHITE_LIST_REQUEST`.
   * - Nếu `defaultIgnoreLogUri = false`: Chỉ lấy danh sách từ `application.yml`.
   * </pre>
   *
   * @return Danh sách URI không cần ghi log.
   */
  public Set<String> getIgnoreLogUri() {
    if (defaultIgnoreLogUri) {
      ignoreLogUri.addAll(RequestConstant.getWhiteListRequest());
    }
    return ignoreLogUri;
  }
}