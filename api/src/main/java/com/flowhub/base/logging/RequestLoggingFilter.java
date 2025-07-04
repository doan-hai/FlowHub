package com.flowhub.base.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.flowhub.base.logging.LoggingUtil.logRequest;

/**
 * **Lớp `RequestLoggingFilter` - Ghi log request trước khi xử lý**
 *
 * <p>Lớp này triển khai `Filter` của Servlet API để tự động ghi log
 * tất cả các request HTTP trước khi chúng được xử lý trong ứng dụng.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi request đi vào hệ thống sẽ được `RequestLoggingFilter` xử lý
 * trước tiên, ghi log thông tin và sau đó chuyển tiếp request đến các filter tiếp theo thông qua
 * `FilterChain`.</p>
 * <p>
 * **📌 Ví dụ log request:**
 * <pre>
 * {@code
 * REQUEST: {"httpMethod": "POST", "httpPath": "/api/orders", "headers": {...}, "parameters": {...}}
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class RequestLoggingFilter implements Filter {

  /** Cấu hình logging để xác định có ghi log request hay không** */
  private final LoggingProperties loggingProperties;

  /**
   * **Constructor khởi tạo `RequestLoggingFilter`**
   *
   * <p>Inject `LoggingProperties` để lấy thông tin cấu hình logging.</p>
   *
   * @param loggingProperties Đối tượng chứa các thiết lập logging.
   */
  public RequestLoggingFilter(LoggingProperties loggingProperties) {
    this.loggingProperties = loggingProperties;
  }

  /**
   * **Ghi log request trước khi chuyển tiếp đến các filter tiếp theo (`doFilter`)**
   *
   * <p>Phương thức này được gọi mỗi khi có request đến hệ thống, giúp ghi log
   * request và sau đó chuyển tiếp đến các filter khác.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Chuyển đổi `ServletRequest` thành `HttpServletRequest`.</li>
   *   <li>📌 Gọi `logRequest()` từ `LoggingUtil` để ghi log request.</li>
   *   <li>📌 Chuyển tiếp request đến các filter tiếp theo trong `FilterChain`.</li>
   * </ul>
   *
   * @param request  Đối tượng `ServletRequest` chứa thông tin request.
   * @param response Đối tượng `ServletResponse` để gửi phản hồi.
   * @param chain    `FilterChain` để tiếp tục xử lý request.
   * @throws ServletException Nếu có lỗi xảy ra trong quá trình xử lý request.
   * @throws IOException      Nếu có lỗi I/O xảy ra khi xử lý request.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    // Ghi log request trước khi xử lý
    logRequest((HttpServletRequest) request, loggingProperties);

    // Tiếp tục chuỗi filter để xử lý request
    chain.doFilter(request, response);
  }
}