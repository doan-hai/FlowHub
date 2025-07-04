package com.flowhub.base.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.base.logging.LoggingProperties;
import com.flowhub.base.utils.RequestUtils;

import static com.flowhub.base.logging.LoggingUtil.logRequest;
import static com.flowhub.base.logging.LoggingUtil.logResponse;
import static com.flowhub.base.utils.JsonUtils.toJson;

/**
 * **Lớp `CustomAccessDeniedHandler` - Xử lý lỗi truy cập bị từ chối (403 Forbidden)**
 *
 * <p>Lớp này triển khai `AccessDeniedHandler` để xử lý các trường hợp người dùng không có quyền
 * truy cập vào tài nguyên bảo vệ trong hệ thống Spring Security.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một người dùng cố gắng truy cập vào một tài nguyên mà họ không có quyền,
 * hệ thống sẽ kích hoạt `handle()` để thực hiện các bước sau:</p>
 * <ul>
 *   <li>📌 Ghi log yêu cầu của người dùng.</li>
 *   <li>📌 Lấy thông tin người dùng từ `SecurityContextHolder`.</li>
 *   <li>📌 Tạo phản hồi JSON chứa thông tin lỗi (`CommonErrorDef.FORBIDDEN`).</li>
 *   <li>📌 Ghi log phản hồi và gửi trả về client.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ phản hồi JSON khi truy cập bị từ chối:**
 * <pre>
 * {
 *     "errorCode": "CM_0003",
 *     "message": "Forbidden",
 *     "detailedMessage": "Bạn không có quyền truy cập tài nguyên này",
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  /** Cấu hình logging để xác định có ghi log request/response hay không** */
  private final LoggingProperties loggingProperties;

  private final MessageSource messageSource;

  /**
   * **Xử lý lỗi truy cập bị từ chối (`handle`)**
   *
   * <p>Phương thức này được gọi khi người dùng không có quyền truy cập vào một tài nguyên
   * được bảo vệ. Nó sẽ trả về phản hồi JSON chứa thông tin lỗi và ghi log lỗi.</p>
   *
   * @param request  Đối tượng `HttpServletRequest` chứa thông tin yêu cầu.
   * @param response Đối tượng `HttpServletResponse` để gửi phản hồi lỗi.
   * @param exc      Ngoại lệ `AccessDeniedException` chứa thông tin lỗi.
   * @throws IOException Nếu có lỗi xảy ra khi ghi phản hồi.
   */
  @Override
  public void handle(
      HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
      throws IOException {
    // Ghi log yêu cầu HTTP trước khi xử lý lỗi
    logRequest(request, loggingProperties);

    // Lấy thông tin người dùng từ SecurityContextHolder
    var auth = SecurityContextHolder.getContext().getAuthentication();

    // Nếu người dùng đã đăng nhập nhưng không có quyền truy cập, ghi log cảnh báo
    if (auth != null) {
      log.warn(
          "Người dùng: {} đã cố gắng truy cập vào URL bị cấm: {}",
          auth.getName(),
          request.getRequestURI());
    }

    // Lấy mã lỗi FORBIDDEN từ danh sách lỗi chung
    var errorCode = CommonErrorDef.FORBIDDEN;

    // Cấu hình response trả về cho client
    response.setCharacterEncoding("UTF-8");
    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    // Viết phản hồi JSON vào response
    response
        .getWriter()
        .write(
            toJson(
                ResponseUtils.getResponseDataError(
                    errorCode.getErrorCode(),
                    exc.getMessage(),
                    messageSource.getMessage(
                        errorCode.getErrorCode(),
                        null,
                        exc.getMessage(),
                        RequestUtils.extractLocale()),
                    null)));

    // Ghi log phản hồi JSON trước khi gửi cho client
    logResponse(request, response, loggingProperties);
  }
}