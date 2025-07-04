package com.flowhub.base.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

/**
 * **Lớp `FeignLogging` - Ghi log request và response trong Feign Client**
 *
 * <p>Lớp này kế thừa `Logger` của Feign để ghi log chi tiết về các request
 * và response trong Feign Client, giúp theo dõi và debug các giao tiếp HTTP.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi request/response đi qua Feign Client sẽ được `FeignLogging` ghi lại
 * với các thông tin như phương thức HTTP, URL, headers, body (nếu có).</p>
 * <p>
 * **📌 Ví dụ log request:**<pre>
 * ```
 * ---> POST https://api.example.com/users HTTP/1.1 (125-byte body)
 * { "name": "John Doe", "email": "john@example.com" }
 * ```
 *
 * **📌 Ví dụ log response:**
 * ```
 * <--- HTTP/1.1 200 OK (300ms) (89-byte body)
 * { "id": 123, "name": "John Doe" }
 * ```</pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
public class FeignLogging extends Logger {

  private static final String BINARY_DATA = "Binary data";

  /**
   * **Ghi log thông tin request HTTP trước khi gửi (`logRequest`)**
   *
   * <p>Phương thức này sẽ ghi lại thông tin của request bao gồm:</p>
   * <ul>
   *   <li>📌 Phương thức HTTP (GET, POST,...).</li>
   *   <li>📌 URL request.</li>
   *   <li>📌 Phiên bản HTTP.</li>
   *   <li>📌 Headers gửi đi.</li>
   *   <li>📌 Kích thước body (nếu có).</li>
   *   <li>📌 Nội dung body request (nếu không phải binary data).</li>
   * </ul>
   *
   * @param configKey Tên phương thức Feign đang gọi.
   * @param logLevel  Mức độ log được cấu hình.
   * @param request   Đối tượng request của Feign.
   */
  @Override
  protected void logRequest(String configKey, Level logLevel, Request request) {
    int bodyLength = 0;
    String bodyText = null;

    // Kiểm tra nếu request có body
    if (request.body() != null) {
      bodyLength = request.body().length;
      bodyText = request.charset() != null ? new String(request.body(), request.charset()) : null;
    }

    // Ghi log request với đầy đủ thông tin
    log(
        configKey,
        "---> %s %s %s %s (%s-byte body) %s",
        request.httpMethod().name(),
        request.url(),
        resolveProtocolVersion(request.protocolVersion()),
        request.headers(),
        bodyLength,
        bodyText != null ? bodyText : BINARY_DATA);
  }

  /**
   * **Ghi log thông tin response HTTP (`logAndRebufferResponse`)**
   *
   * <p>Phương thức này sẽ ghi lại thông tin của response bao gồm:</p>
   * <ul>
   *   <li>📌 Mã trạng thái HTTP (200, 404,...).</li>
   *   <li>📌 Thời gian phản hồi.</li>
   *   <li>📌 Headers của response.</li>
   *   <li>📌 Kích thước body response (nếu có).</li>
   *   <li>📌 Nội dung body response (nếu không phải binary data).</li>
   * </ul>
   *
   * <p>Ngoài ra, phương thức này cũng "rebuffer" response body để có thể đọc lại dữ liệu.</p>
   *
   * @param configKey   Tên phương thức Feign đang gọi.
   * @param logLevel    Mức độ log được cấu hình.
   * @param response    Đối tượng response của Feign.
   * @param elapsedTime Thời gian xử lý request (ms).
   * @return Đối tượng `Response` có thể đọc lại dữ liệu.
   * @throws IOException Nếu có lỗi khi đọc response.
   */
  @Override
  protected Response logAndRebufferResponse(
      String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
    int status = response.status();
    int bodyLength = 0;

    // Kiểm tra nếu response có body và không phải là HTTP 204 hoặc 205
    if (response.body() != null && !(status == 204 || status == 205)) {
      byte[] bodyData = Util.toByteArray(response.body().asInputStream());
      bodyLength = bodyData.length;

      if (bodyLength > 0) {
        log(
            configKey,
            "<--- %s %s%s (%sms) %s (%s-byte body) %s",
            resolveProtocolVersion(response.protocolVersion()),
            status,
            response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? StringUtils.SPACE
                + response.reason() : StringUtils.EMPTY,
            elapsedTime,
            response.headers(),
            bodyLength,
            decodeOrDefault(bodyData, UTF_8, BINARY_DATA));
      }

      // Trả về response đã được buffer lại để có thể đọc lại nội dung
      return response.toBuilder().body(bodyData).build();
    } else {
      String logData = StringUtils.EMPTY;

      if (response.body() != null) {
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        logData = decodeOrDefault(bodyData, UTF_8, BINARY_DATA);
      }

      log(
          configKey,
          "<--- %s %s%s (%sms) %s (%s-byte body) %s",
          resolveProtocolVersion(response.protocolVersion()),
          status,
          response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? StringUtils.SPACE
              + response.reason()
              : StringUtils.EMPTY,
          elapsedTime,
          response.headers(),
          bodyLength,
          logData);
    }

    return response;
  }

  /**
   * **Ghi log theo định dạng tùy chỉnh (`log`)**
   *
   * <p>Phương thức này dùng để log thông tin request và response theo định dạng chuẩn.</p>
   *
   * @param configKey Tên phương thức Feign đang gọi.
   * @param format    Định dạng log.
   * @param args      Danh sách đối số để format log.
   */
  @Override
  protected void log(String configKey, String format, Object... args) {
    log.info(format(configKey, format, args));
  }

  /**
   * **Tạo chuỗi log theo định dạng (`format`)**
   *
   * <p>Phương thức này giúp định dạng log với tên phương thức Feign.</p>
   *
   * @param configKey Tên phương thức Feign đang gọi.
   * @param format    Định dạng log.
   * @param args      Danh sách đối số để format log.
   * @return Chuỗi log đã được format.
   */
  protected String format(String configKey, String format, Object... args) {
    return String.format(methodTag(configKey) + format, args);
  }
}