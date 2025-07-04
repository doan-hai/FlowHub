package com.flowhub.base.constant;

import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;


/**
 * **Lớp chứa các hằng số dùng trong hệ thống để xử lý yêu cầu (request) từ client
 * (`RequestConstant`)**
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các hằng số này có thể được sử dụng trong bộ lọc bảo mật (`SecurityFilter`), bộ log request,
 * hoặc trong việc xác thực người dùng.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong Spring Security để bỏ qua xác thực cho danh sách
 * `IGNORE_AUTHENTICATION_PATTERN`:**
 * <pre>
 * {@code
 * http.authorizeRequests()
 *     .requestMatchers(RequestConstant.IGNORE_AUTHENTICATION_PATTERN).permitAll()
 *     .anyRequest().authenticated();
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ sử dụng để lấy IP thực của client từ request:**
 * <pre>
 * {@code
 * for (String header : RequestConstant.HEADERS_TO_TRY) {
 *     String ip = request.getHeader(header);
 *     if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
 *         return ip;
 *     }
 * }
 * return request.getRemoteAddr();
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
public class RequestConstant {

  public static final String REQUEST_ID = "X-Request-ID";

  public static final String CLIENT_IP = "X-Client-IP";

  public static final String CLIENT_TIME = "X-Client-Time";

  public static final String CLIENT_TIMEZONE = "X-Client-Timezone";

  public static final String CLIENT_LANG = "X-Lang";

  public static final String LOCAL_IP = "X-Local-IP";

  public static final String MESSAGE_EVENT = "MessageListenerEvent";

  public static final String BROKER_TYPE = "EventBrokerType";

  public static final String BROKER_KAFKA = "KAFKA";

  public static final String BROKER_RABBIT = "RABBIT";

  public static final String SERVICE_NAME = "service-name";

  public static final String REQUEST_TIME_START = "time-start";

  public static final String AUTHORIZATION = "Authorization";

  public static final String REQUEST_LOGGING = "logging";

  public static final String BEARER_PREFIX = "Bearer ";

  public static final String BASIC_PREFIX = "Basic ";

  public static final String SECURE_API_KEY = "secure-api-key";

  public static final String SESSION_ID = "sessionId";

  public static final String DEVICE_ID = "X-Device-ID";

  public static final String DEVICE_TOKEN = "X-Device-Token";

  public static final String DEVICE_NAME = "X-Device-Name";

  public static final String DEVICE_TYPE = "X-Device-Type";

  public static final String BROWSER_NAME = "X-Browser-Name";

  public static final String APPLICATION_VERSION = "X-Application-Version";

  public static final String SYSTEM = "SYSTEM";

  public static final String ROLE_SYSTEM = "ROLE_SYSTEM";

  private static final List<String> WHITE_LIST_REQUEST =
      Arrays.asList(
          "/swagger-ui.html/**",
          "/swagger-resources/**",
          "/webjars/**",
          "/swagger-ui.html#!/**",
          "/v2/api-docs",
          "/v3/api-docs",
          "/v3/api-docs/**",
          "/actuator/**",
          "/css/**",
          "/js/**",
          "/**/*.png",
          "/**/*.gif",
          "/**/*.svg",
          "/**/*.jpg",
          "/**/*.html",
          "/**/*.css",
          "/**/*.js",
          "/favicon.ico",
          "/",
          "/error",
          "/csrf");

  public static List<String> getWhiteListRequest() {
    return WHITE_LIST_REQUEST;
  }

  private static final String[] HEADERS_TO_TRY = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  };

  /**
   * **Hàm lấy danh sách các header để lấy IP thực của client**
   *
   * @return Danh sách các header để lấy IP thực của client
   */
  public static String[] getHeadersToTry() {
    return HEADERS_TO_TRY;
  }

  private static final String[] IGNORE_AUTHENTICATION_PATTERN = {
      "/",
      "/public/**",
      "/internal/**",
      "/error",
      "/favicon.ico",
      "/**/*.png",
      "/**/*.gif",
      "/**/*.svg",
      "/**/*.jpg",
      "/**/*.html",
      "/**/*.css",
      "/**/*.js",
      "/swagger-ui.html/**",
      "/swagger-ui/**",
      "/swagger-resources/**",
      "/v3/api-docs",
      "/v3/api-docs/**",
      "/webjars/**",
      "/csrf",
      "/actuator",
      "/actuator/**"
  };

  /**
   * **Hàm lấy danh sách các pattern không cần xác thực**
   *
   * @return Danh sách các pattern không cần xác thực
   */
  public static String[] getIgnoreAuthenticationPattern() {
    return IGNORE_AUTHENTICATION_PATTERN;
  }
}
