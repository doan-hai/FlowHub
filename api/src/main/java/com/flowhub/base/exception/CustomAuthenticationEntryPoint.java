package com.flowhub.base.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.base.logging.LoggingProperties;
import com.flowhub.base.utils.RequestUtils;

import static com.flowhub.base.exception.CommonErrorDef.FORBIDDEN;
import static com.flowhub.base.logging.LoggingUtil.logRequest;
import static com.flowhub.base.logging.LoggingUtil.logResponse;
import static com.flowhub.base.utils.JsonUtils.toJson;

/**
 * **Lớp `CustomAuthenticationEntryPoint` - Xử lý lỗi xác thực không hợp lệ (401 Unauthorized)**
 *
 * <p>Lớp này triển khai `AuthenticationEntryPoint` để xử lý các trường hợp người dùng
 * chưa xác thực hoặc xác thực thất bại khi truy cập tài nguyên bảo vệ trong hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một người dùng chưa xác thực hoặc không hợp lệ cố gắng truy cập vào tài nguyên bảo vệ,
 * hệ thống sẽ kích hoạt `commence()` để thực hiện các bước sau:</p>
 * <ul>
 *   <li>📌 Ghi log yêu cầu của người dùng.</li>
 *   <li>📌 Ghi log cảnh báo về việc cần đăng nhập.</li>
 *   <li>📌 Tạo phản hồi JSON chứa thông tin lỗi (`FORBIDDEN`).</li>
 *   <li>📌 Ghi log phản hồi và gửi trả về client.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ phản hồi JSON khi xác thực thất bại:**
 * <pre>
 * {
 *     "errorCode": "CM_0003",
 *     "message": "Forbidden",
 *     "detailedMessage": "Bạn cần đăng nhập trước khi thực hiện hành động này.",
 *     "data": null
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /** Cấu hình logging để xác định có ghi log request/response hay không** */
  private final LoggingProperties loggingProperties;

  private final MessageSource messageSource;

  /**
   * **Xử lý lỗi xác thực không hợp lệ (`commence`)**
   *
   * <p>Phương thức này được gọi khi người dùng chưa đăng nhập hoặc đăng nhập thất bại.
   * Nó sẽ trả về phản hồi JSON chứa thông tin lỗi và ghi log lỗi.</p>
   *
   * @param request       Đối tượng `HttpServletRequest` chứa thông tin yêu cầu.
   * @param response      Đối tượng `HttpServletResponse` để gửi phản hồi lỗi.
   * @param authException Ngoại lệ `AuthenticationException` chứa thông tin lỗi.
   * @throws IOException Nếu có lỗi xảy ra khi ghi phản hồi.
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    // Ghi log yêu cầu HTTP trước khi xử lý lỗi
    logRequest(request, loggingProperties);

    // Ghi log cảnh báo yêu cầu đăng nhập
    log.warn("Bạn cần đăng nhập trước khi thực hiện hành động này.");

    // Lấy mã lỗi FORBIDDEN từ danh sách lỗi chung
    var errorCode = FORBIDDEN;

    // Cấu hình response trả về cho client
    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // Viết phản hồi JSON vào response
    response
        .getWriter()
        .print(
            toJson(
                ResponseUtils.getResponseDataError(
                    errorCode.getErrorCode(),
                    authException.getMessage(),
                    messageSource.getMessage(
                        errorCode.getErrorCode(),
                        null,
                        authException.getMessage(),
                        RequestUtils.extractLocale()),
                    null)));

    // Ghi log phản hồi JSON trước khi gửi cho client
    logResponse(request, response, loggingProperties);
  }
}