package com.flowhub.base.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * **Lớp `LoggingResponseBodyAdviceAdapter` - Ghi log dữ liệu phản hồi từ API**
 *
 * <p>Lớp này triển khai `ResponseBodyAdvice` để can thiệp vào quá trình xử lý response,
 * giúp ghi log nội dung phản hồi trước khi gửi về client.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi khi một response được trả về từ controller, phương thức `beforeBodyWrite()` sẽ được gọi.
 * Nếu request là một `ServletServerHttpRequest`, hệ thống sẽ ghi log thông qua
 * `LoggingUtil.logResponse()`.</p>
 * <pre>
 * **📌 Ví dụ log response:**
 * ```
 * <--- HTTP/1.1 200 OK (120ms)
 * Content-Type: application/json
 * Body: {"status": "success", "data": {...}}
 * ```
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@ControllerAdvice
public class LoggingResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

  /** Thuộc tính logging để kiểm soát việc ghi log response** */
  private final LoggingProperties loggingProperties;

  /**
   * **Constructor khởi tạo `LoggingResponseBodyAdviceAdapter`**
   *
   * <p>Inject `LoggingProperties` để xác định có ghi log response hay không.</p>
   *
   * @param loggingProperties Đối tượng chứa cấu hình logging.
   */
  @Autowired
  public LoggingResponseBodyAdviceAdapter(LoggingProperties loggingProperties) {
    this.loggingProperties = loggingProperties;
  }

  /**
   * **Xác định class nào sẽ áp dụng logic ghi log (`supports`)**
   *
   * <p>Phương thức này quyết định xem response có cần được xử lý không.
   * Ở đây, nó luôn trả về `true` để áp dụng cho tất cả response.</p>
   *
   * @param returnType    Kiểu dữ liệu trả về từ controller.
   * @param converterType Loại `HttpMessageConverter` được sử dụng.
   * @return Luôn trả về `true`, nghĩa là áp dụng cho tất cả response.
   */
  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  /**
   * **Ghi log dữ liệu response trước khi trả về client (`beforeBodyWrite`)**
   *
   * <p>Phương thức này sẽ ghi log nội dung response nếu request là `ServletServerHttpRequest`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Nếu request là `ServletServerHttpRequest`, gọi `LoggingUtil.logResponse()` để ghi log.</li>
   *   <li>📌 Trả về nội dung gốc của response để tiếp tục xử lý bình thường.</li>
   * </ul>
   *
   * @param body                  Nội dung phản hồi từ controller.
   * @param returnType            Kiểu dữ liệu phản hồi từ controller.
   * @param selectedContentType   Loại nội dung phản hồi (JSON, XML,...).
   * @param selectedConverterType Kiểu `HttpMessageConverter` được sử dụng.
   * @param request               Đối tượng `ServerHttpRequest` chứa thông tin request.
   * @param response              Đối tượng `ServerHttpResponse` chứa thông tin response.
   * @return Nội dung phản hồi gốc, không bị thay đổi.
   */
  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    // **📌 Kiểm tra nếu request là ServletServerHttpRequest thì ghi log**
    if (request instanceof ServletServerHttpRequest serverHttpRequest
        && response instanceof ServletServerHttpResponse serverHttpResponse) {
      LoggingUtil.logResponse(
          serverHttpRequest.getServletRequest(),
          serverHttpResponse.getServletResponse(),
          loggingProperties,
          body);
    }

    // Trả về body gốc để tiếp tục xử lý bình thường
    return body;
  }
}