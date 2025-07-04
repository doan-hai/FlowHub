package com.flowhub.base.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.utils.JsonUtils;
import com.flowhub.base.utils.RequestUtils;

/**
 * **Lớp `LoggingUtil` - Hỗ trợ ghi log request và response**
 *
 * <p>Lớp tiện ích này cung cấp các phương thức để ghi log chi tiết request
 * và response trong hệ thống, giúp theo dõi và debug dễ dàng hơn.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một request hoặc response được xử lý trong ứng dụng, các phương thức trong lớp này
 * sẽ kiểm tra xem request có cần ghi log không, sau đó trích xuất thông tin cần thiết và ghi log
 * dưới dạng JSON để dễ dàng phân tích.</p>
 * <p>
 * **📌 Ví dụ log request:**
 * <pre>
 * {@code
 * REQUEST: {"httpMethod": "GET", "httpPath": "/api/users", "headers": {...}, "parameters": {...}}
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ log response:**
 * <pre>
 * {@code
 * RESPONSE: {"responseCode": 200, "during": "0.123", "headers": {...}, "body": {...}}
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
public class LoggingUtil {

  private static final String LOG_REQUEST_PREFIX = "REQUEST: {}";

  private static final String LOG_REQUEST_BODY_PREFIX = "REQUEST BODY: {}";

  private static final String LOG_RESPONSE_PREFIX = "RESPONSE: {}";

  private static final String LOG_RESPONSE_SUFFIX = "...";

  private LoggingUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * **Ghi log thông tin request (không bao gồm body)**
   *
   * <p>Phương thức này ghi log thông tin cơ bản của request, bao gồm:</p>
   * <ul>
   *   <li>📌 HTTP method (GET, POST,...)</li>
   *   <li>📌 Đường dẫn request</li>
   *   <li>📌 Headers của request</li>
   *   <li>📌 Tham số request</li>
   * </ul>
   *
   * @param servletRequest    Request từ client.
   * @param loggingProperties Cấu hình logging.
   */
  public static void logRequest(
      HttpServletRequest servletRequest, LoggingProperties loggingProperties) {
    if (RequestUtils.matches(servletRequest, loggingProperties.getIgnoreLogUri())) {
      servletRequest.setAttribute(RequestConstant.REQUEST_LOGGING, false);
      return;
    }
    servletRequest.setAttribute(RequestConstant.REQUEST_LOGGING, true);
    var requestObject =
        LogRequestObject.builder()
                        .headers(buildHeadersMap(servletRequest))
                        .httpMethod(servletRequest.getMethod())
                        .httpPath(servletRequest.getRequestURI())
                        .parameters(buildParametersMap(servletRequest))
                        .build();
    var message = JsonUtils.toJson(requestObject);
    log.info(LOG_REQUEST_PREFIX, message);
  }

  /**
   * **Ghi log request body nếu có**
   *
   * <p>Ngoài các thông tin cơ bản, phương thức này cũng ghi log nội dung request body
   * nếu request có chứa dữ liệu.</p>
   *
   * @param servletRequest    Request từ client.
   * @param loggingProperties Cấu hình logging.
   * @param body              Nội dung body của request.
   */
  public static void logRequest(
      HttpServletRequest servletRequest, LoggingProperties loggingProperties, Object body) {
    if (isNotLogging(servletRequest, loggingProperties.getIgnoreLogUri())) {
      return;
    }
    if (body instanceof String) {
      log.info(LOG_REQUEST_BODY_PREFIX, body);
    } else {
      String message = JsonUtils.toJson(body);
      log.info(LOG_REQUEST_BODY_PREFIX, message);
    }
  }

  /**
   * **Ghi log response**
   *
   * <p>Ghi log thông tin response mà không bao gồm body.</p>
   *
   * @param servletRequest    Request ban đầu từ client.
   * @param servletResponse   Response được trả về cho client.
   * @param loggingProperties Cấu hình logging.
   */
  public static void logResponse(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      LoggingProperties loggingProperties) {
    logResponse(servletRequest, servletResponse, loggingProperties, null);
  }

  /**
   * **Ghi log response (bao gồm body nếu có)**
   *
   * <p>Phương thức này ghi log response bao gồm:</p>
   * <ul>
   *   <li>📌 Mã trạng thái HTTP</li>
   *   <li>📌 Headers response</li>
   *   <li>📌 Thời gian xử lý request</li>
   *   <li>📌 Nội dung body (nếu không bị giới hạn bởi loggingProperties)</li>
   * </ul>
   *
   * @param servletRequest    Request từ client.
   * @param servletResponse   Response được trả về cho client.
   * @param loggingProperties Cấu hình logging.
   * @param object            Dữ liệu body của response.
   */
  public static void logResponse(
      HttpServletRequest servletRequest,
      HttpServletResponse servletResponse,
      LoggingProperties loggingProperties,
      Object object) {
    if (isNotLogging(servletRequest, loggingProperties.getIgnoreLogUri())) {
      return;
    }
    Object o = servletRequest.getAttribute(RequestConstant.REQUEST_TIME_START);
    var during = o == null ? 0 : (System.currentTimeMillis() - (long) o);
    var responseObject =
        LogResponseObject.builder()
                         .responseCode(servletResponse.getStatus())
                         .during(String.format("%.3f", (double) during / 1000))
                         .headers(buildHeadersMap(servletResponse))
                         .body(loggingProperties.isExcludeResponseBody() ? object : null)
                         .build();
    var str = JsonUtils.toJson(responseObject);
    if (str.length() > loggingProperties.getResponseMaxPayloadLength()) {
      str = str.substring(0, loggingProperties.getResponseMaxPayloadLength()) + LOG_RESPONSE_SUFFIX;
    }
    log.info(LOG_RESPONSE_PREFIX, str);
    ThreadContext.clearAll();
  }

  /**
   * **Kiểm tra request có cần ghi log không**
   *
   * @param servletRequest Request từ client.
   * @param ignorePatterns Danh sách URI không cần ghi log.
   * @return `true` nếu request không cần ghi log, `false` nếu cần ghi log.
   */
  private static boolean isNotLogging(
      HttpServletRequest servletRequest, Set<String> ignorePatterns) {
    var isLog = servletRequest.getAttribute(RequestConstant.REQUEST_LOGGING);
    if (isLog == null) {
      return RequestUtils.matches(servletRequest, ignorePatterns);
    }
    return !(boolean) isLog;
  }

  /**
   * **Tạo danh sách tham số từ request (`buildParametersMap`)**
   *
   * <p>Phương thức này lấy tất cả các tham số từ request và đưa vào một `Map<String, String>`,
   * giúp dễ dàng ghi log hoặc xử lý dữ liệu.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả tham số trong request.</li>
   *   <li>📌 Lưu cặp `key-value` vào `HashMap`.</li>
   *   <li>📌 Trả về danh sách tham số dưới dạng `Map<String, String>`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * Request: GET /api/users?name=John&age=30
   * Trả về: {"name": "John", "age": "30"}
   * }
   * </pre>
   *
   * @param servletRequest Request từ client.
   * @return `Map<String, String>` chứa danh sách tham số request.
   */
  private static Map<String, String> buildParametersMap(HttpServletRequest servletRequest) {
    Map<String, String> resultMap = new HashMap<>();
    var parameterNames = servletRequest.getParameterNames();

    while (parameterNames.hasMoreElements()) {
      var key = parameterNames.nextElement();
      var value = servletRequest.getParameter(key);
      resultMap.put(key, value);
    }

    return resultMap;
  }

  /**
   * **Tạo danh sách headers từ request (`buildHeadersMap`)**
   *
   * <p>Phương thức này lấy tất cả headers từ request và đưa vào `Map<String, String>`,
   * giúp dễ dàng ghi log hoặc xử lý dữ liệu.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả headers trong request.</li>
   *   <li>📌 Bỏ qua header `Authorization` để tránh ghi log dữ liệu nhạy cảm.</li>
   *   <li>📌 Lưu cặp `key-value` vào `HashMap`.</li>
   *   <li>📌 Trả về danh sách headers dưới dạng `Map<String, String>`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * Headers request:
   *   Authorization: Bearer abc123
   *   User-Agent: PostmanRuntime/7.29.0
   *
   * Trả về:
   * {
   *   "Authorization": "<<Not log authorization record>>",
   *   "User-Agent": "PostmanRuntime/7.29.0"
   * }
   * }
   * </pre>
   *
   * @param servletRequest Request từ client.
   * @return `Map<String, String>` chứa danh sách headers của request.
   */
  private static Map<String, String> buildHeadersMap(HttpServletRequest servletRequest) {
    Map<String, String> map = new HashMap<>();
    var headerNames = servletRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      var key = headerNames.nextElement();
      if (key.equalsIgnoreCase(RequestConstant.AUTHORIZATION)) {
        map.put(key, "<<Not log authorization record>>");
        continue;
      }
      map.put(key, servletRequest.getHeader(key));
    }
    return map;
  }

  /**
   * **Tạo danh sách headers từ response (`buildHeadersMap`)**
   *
   * <p>Phương thức này lấy tất cả headers từ response và đưa vào `Map<String, String>`,
   * giúp dễ dàng ghi log hoặc xử lý dữ liệu.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả headers trong response.</li>
   *   <li>📌 Lưu cặp `key-value` vào `HashMap`.</li>
   *   <li>📌 Trả về danh sách headers dưới dạng `Map<String, String>`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * Headers response:
   *   Content-Type: application/json
   *   Cache-Control: no-cache
   *
   * Trả về:
   * {
   *   "Content-Type": "application/json",
   *   "Cache-Control": "no-cache"
   * }
   * }
   * </pre>
   *
   * @param servletResponse Response từ server.
   * @return `Map<String, String>` chứa danh sách headers của response.
   */
  private static Map<String, String> buildHeadersMap(HttpServletResponse servletResponse) {
    Map<String, String> map = new HashMap<>();
    var headerNames = servletResponse.getHeaderNames();
    for (String header : headerNames) {
      map.put(header, servletResponse.getHeader(header));
    }
    return map;
  }

  /**
   * **Lớp `LogRequestObject` - Định nghĩa thông tin request cần ghi log**
   *
   * <p>Lớp này chứa các thông tin quan trọng của request để ghi log, bao gồm:</p>
   * <ul>
   *   <li>📌 `httpMethod` - Phương thức HTTP (GET, POST,...).</li>
   *   <li>📌 `httpPath` - Đường dẫn của request.</li>
   *   <li>📌 `headers` - Danh sách headers của request.</li>
   *   <li>📌 `parameters` - Danh sách tham số request.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ JSON log request:**
   * <pre>
   * {@code
   * {
   *   "httpMethod": "POST",
   *   "httpPath": "/api/users",
   *   "headers": {
   *     "Content-Type": "application/json"
   *   },
   *   "parameters": {
   *     "id": "123"
   *   }
   * }
   * }
   * </pre>
   */
  @RequiredArgsConstructor
  @Builder
  @Getter
  public static class LogRequestObject {

    private final String httpMethod;

    private final String httpPath;

    private final Map<String, String> headers;

    private final Map<String, String> parameters;
  }

  /**
   * **Lớp `LogResponseObject` - Định nghĩa thông tin response cần ghi log**
   *
   * <p>Lớp này chứa các thông tin quan trọng của response để ghi log, bao gồm:</p>
   * <ul>
   *   <li>📌 `responseCode` - Mã trạng thái HTTP (200, 400,...).</li>
   *   <li>📌 `during` - Thời gian xử lý request.</li>
   *   <li>📌 `headers` - Danh sách headers của response.</li>
   *   <li>📌 `body` - Nội dung body của response.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ JSON log response:**
   * <pre>
   * {@code
   * {
   *   "responseCode": 200,
   *   "during": "0.123",
   *   "headers": {
   *     "Content-Type": "application/json"
   *   },
   *   "body": {
   *     "status": "success",
   *     "data": {...}
   *   }
   * }
   * }
   * </pre>
   */
  @RequiredArgsConstructor
  @Builder
  @Getter
  public static class LogResponseObject {

    private final int responseCode;

    private final String during;

    private final Map<String, String> headers;

    private final Object body;
  }
}
