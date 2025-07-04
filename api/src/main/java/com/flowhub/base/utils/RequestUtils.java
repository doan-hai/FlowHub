package com.flowhub.base.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import com.flowhub.base.constant.RequestConstant;

/**
 * **Lớp `RequestUtils` - Hỗ trợ xử lý thông tin từ request**
 *
 * <p>Class tiện ích này cung cấp các phương thức để lấy thông tin từ request,
 * kiểm tra đường dẫn, header, IP client, token và các dữ liệu liên quan.</p>
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
@Slf4j
public class RequestUtils {

  private static final String VERSION = "version";

  private static final String OPERA = "opera";

  /**
   * **Trích xuất hệ điều hành từ request (`extractOs`)**
   *
   * <p>Phương thức này đọc header `User-Agent` từ request và xác định hệ điều hành của client.</p>
   * <p>
   * **📌 Các hệ điều hành có thể xác định:**
   * <pre>
   * - Windows
   * - Mac
   * - Unix
   * - Android
   * - iPhone
   * - Không xác định (`UnKnown`)
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String os = RequestUtils.extractOs(request);
   * System.out.println(os); // Windows, Mac, Unix, Android, iPhone, UnKnown
   * }
   * </pre>
   *
   * @param request Request từ client.
   * @return Tên hệ điều hành.
   */
  public static String extractOs(HttpServletRequest request) {
    var os = StringUtils.EMPTY;
    if (request == null) {
      return os;
    }
    var userAgent = request.getHeader("User-Agent");
    if (userAgent.toLowerCase().contains("windows")) {
      os = "Windows";
    } else if (userAgent.toLowerCase().contains("mac")) {
      os = "Mac";
    } else if (userAgent.toLowerCase().contains("x11")) {
      os = "Unix";
    } else if (userAgent.toLowerCase().contains("android")) {
      os = "Android";
    } else if (userAgent.toLowerCase().contains("iphone")) {
      os = "IPhone";
    } else {
      os = "UnKnown, More-Info: " + userAgent;
    }
    return os;
  }

  /**
   * **Trích xuất trình duyệt từ request (`extractBrowser`)**
   *
   * <p>Phương thức này đọc header `User-Agent` từ request và xác định trình duyệt của client.</p>
   * <p>
   * **📌 Các trình duyệt có thể xác định:**
   * <pre>
   * - Internet Explorer (IE)
   * - Safari
   * - Opera
   * - Chrome
   * - Firefox
   * - Netscape
   * - Không xác định (`UnKnown`)
   * </pre>
   *
   * @param request Request từ client.
   * @return Tên trình duyệt và phiên bản (nếu có).
   */
  public static String extractBrowser(HttpServletRequest request) {
    var browser = StringUtils.EMPTY;
    if (request == null) {
      return browser;
    }
    var userAgent = request.getHeader("User-Agent").toLowerCase();
    if (userAgent.contains("msie")) {
      var substring = userAgent.substring(userAgent.indexOf("msie")).split(";")[0];
      browser =
          substring.split(StringUtils.SPACE)[0].replace("msie", "IE")
              + "-"
              + substring.split(StringUtils.SPACE)[1];
    } else if (userAgent.contains("safari") && userAgent.contains(VERSION)) {
      browser =
          (userAgent.substring(userAgent.indexOf("safari")).split(StringUtils.SPACE)[0])
              .split("/")[0]
              + "-"
              + (userAgent.substring(userAgent.indexOf(VERSION)).split(StringUtils.SPACE)[0])
              .split("/")[1];
    } else if (userAgent.contains("opr") || userAgent.contains(OPERA)) {
      if (userAgent.contains(OPERA)) {
        browser =
            (userAgent.substring(userAgent.indexOf(OPERA)).split(StringUtils.SPACE)[0])
                .split("/")[0]
                + "-"
                + (userAgent.substring(userAgent.indexOf(VERSION)).split(StringUtils.SPACE)[0])
                .split("/")[1];
      } else if (userAgent.contains("opr")) {
        browser =
            ((userAgent.substring(userAgent.indexOf("opr")).split(StringUtils.SPACE)[0])
                .replace("/", "-"))
                .replace("opr", "Opera");
      }
    } else if (userAgent.contains("chrome")) {
      browser =
          (userAgent.substring(userAgent.indexOf("chrome")).split(StringUtils.SPACE)[0])
              .replace("/", "-");
    } else if ((userAgent.contains("mozilla/7.0"))
        || (userAgent.contains("netscape6"))
        || (userAgent.contains("mozilla/4.7"))
        || (userAgent.contains("mozilla/4.78"))
        || (userAgent.contains("mozilla/4.08"))
        || (userAgent.contains("mozilla/3"))) {
      browser = "Netscape-?";
    } else if (userAgent.contains("firefox")) {
      browser =
          (userAgent.substring(userAgent.indexOf("firefox")).split(StringUtils.SPACE)[0])
              .replace("/", "-");
    } else if (userAgent.contains("rv")) {
      browser = "IE-" + userAgent.substring(userAgent.indexOf("rv") + 3, userAgent.indexOf(")"));
    } else {
      browser = "UnKnown, More-Info: " + userAgent;
    }
    return browser;
  }

  /**
   * **Trích xuất địa chỉ IP của client (`extractClientIpAddress`)**
   *
   * <p>Phương thức này lấy IP từ các header HTTP phổ biến hoặc `request.getRemoteAddr()`.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String clientIp = RequestUtils.extractClientIpAddress(request);
   * System.out.println(clientIp); // 192.168.1.1
   * }
   * </pre>
   *
   * @param request Request từ client.
   * @return Địa chỉ IP của client.
   */
  public static String extractClientIpAddress(HttpServletRequest request) {
    for (String header : RequestConstant.getHeadersToTry()) {
      var ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        return ip;
      }
    }
    return request.getRemoteAddr();
  }

  /**
   * **Kiểm tra request có khớp với danh sách mẫu đường dẫn cần loại trừ (`matches`)**
   *
   * <p>Phương thức này kiểm tra xem request có thuộc danh sách các đường dẫn bị loại trừ
   * khỏi xử lý hay không bằng cách so sánh với `excludePatterns`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Nếu `request.getContextPath()` trống, kiểm tra trực tiếp `request.getRequestURI()`.
   * - Nếu có context path, kiểm tra phần URI sau context path.
   * - Sử dụng `matches(String lookupPath, Set<String> excludePatterns)` để thực hiện so khớp.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * boolean isExcluded = RequestUtils.matches(request, Set.of("/api/health", "/api/status"));
   * System.out.println(isExcluded); // true nếu request là /api/health hoặc /api/status
   * }
   * </pre>
   *
   * @param request         Request HTTP cần kiểm tra.
   * @param excludePatterns Danh sách đường dẫn cần loại trừ.
   * @return `true` nếu request khớp với một trong các mẫu loại trừ, `false` nếu không.
   */
  public static boolean matches(HttpServletRequest request, Set<String> excludePatterns) {
    if (StringUtils.isEmpty(request.getContextPath())) {
      return matches(request.getRequestURI(), excludePatterns);
    }
    return matches(
        request.getRequestURI().substring(request.getContextPath().length()), excludePatterns);
  }

  /**
   * **Kiểm tra đường dẫn có khớp với danh sách mẫu đường dẫn (`matches`)**
   *
   * <p>Phương thức này kiểm tra xem đường dẫn `lookupPath` có khớp với bất kỳ mẫu nào
   * trong danh sách `excludePatterns` hay không.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Sử dụng `AntPathMatcher` để kiểm tra đường dẫn có khớp với pattern không.
   * - Nếu `excludePatterns` rỗng, trả về `false`.
   * - Nếu `lookupPath` khớp với một mẫu trong `excludePatterns`, trả về `true`.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * boolean isExcluded = RequestUtils.matches("/api/health", Set.of("/api/health", "/api/status"));
   * System.out.println(isExcluded); // true
   * }
   * </pre>
   *
   * @param lookupPath      Đường dẫn cần kiểm tra.
   * @param excludePatterns Danh sách đường dẫn cần loại trừ.
   * @return `true` nếu `lookupPath` khớp với một mẫu trong danh sách, `false` nếu không.
   */
  public static boolean matches(String lookupPath, Set<String> excludePatterns) {
    PathMatcher pathMatcherToUse = new AntPathMatcher();
    if (!CollectionUtils.isEmpty(excludePatterns)) {
      for (String pattern : excludePatterns) {
        if (pathMatcherToUse.match(pattern, lookupPath)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * **Kiểm tra request có chứa request body (`existedRequestBody`)**
   *
   * <p>Phương thức này kiểm tra xem phương thức xử lý request có tham số được đánh dấu
   * `@RequestBody` không.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Lấy danh sách các tham số của phương thức.
   * - Kiểm tra xem có annotation `@RequestBody` nào không.
   * - Nếu có ít nhất một tham số có `@RequestBody`, trả về `true`, ngược lại trả về `false`.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * boolean hasRequestBody = RequestUtils.existedRequestBody(handlerMethod);
   * System.out.println(hasRequestBody); // true nếu có tham số @RequestBody
   * }
   * </pre>
   *
   * @param o Đối tượng `HandlerMethod` chứa thông tin phương thức xử lý request.
   * @return `true` nếu phương thức có tham số `@RequestBody`, `false` nếu không.
   */
  public static boolean existedRequestBody(Object o) {
    try {
      var method = ((HandlerMethod) o).getMethod();
      var annotations = method.getParameterAnnotations();
      for (Annotation[] annotation : annotations) {
        for (Annotation tmp : annotation) {
          if (tmp instanceof RequestBody) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  /**
   * **Trích xuất header xác thực từ request (`extractAuthentication`)**
   *
   * <p>Phương thức này lấy giá trị của header `Authorization` từ request,
   * giúp kiểm tra thông tin xác thực.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String authHeader = RequestUtils.extractAuthentication(request);
   * System.out.println(authHeader); // "Bearer abc123xyz"
   * }
   * </pre>
   *
   * @param servletRequest Request từ client.
   * @return Chuỗi chứa giá trị của header `Authorization` hoặc `null` nếu không có.
   */
  public static String extractAuthentication(HttpServletRequest servletRequest) {
    return servletRequest.getHeader(RequestConstant.AUTHORIZATION);
  }

  /**
   * **Trích xuất token xác thực từ request (`extractToken`)**
   *
   * <p>Phương thức này lấy token từ header `Authorization`, hỗ trợ cả
   * `Bearer` và `Basic` authentication.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Lấy header `Authorization`.
   * - Nếu header có tiền tố `Bearer ` hoặc `Basic `, loại bỏ tiền tố và trả về token.
   * - Nếu không có tiền tố, trả về nguyên bản giá trị của `Authorization`.
   * - Nếu không có header `Authorization`, trả về `null`.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String token = RequestUtils.extractToken(request);
   * System.out.println(token); // "abc123xyz"
   * }
   * </pre>
   *
   * @param servletRequest Request từ client.
   * @return Chuỗi token hoặc `null` nếu không có.
   */
  public static String extractToken(HttpServletRequest servletRequest) {
    var auth = extractAuthentication(servletRequest);
    if (auth != null) {
      if (auth.startsWith(RequestConstant.BEARER_PREFIX)) {
        return auth.replace(RequestConstant.BEARER_PREFIX, StringUtils.EMPTY);
      }
      if (auth.startsWith(RequestConstant.BASIC_PREFIX)) {
        return auth.replace(RequestConstant.BASIC_PREFIX, StringUtils.EMPTY);
      }
      return auth;
    }
    return null;
  }

  /**
   * **Lấy `requestId` từ `ThreadContext` (`extractRequestId`)**
   *
   * <p>Phương thức này lấy `requestId` đã được gán vào `ThreadContext`,
   * giúp theo dõi request xuyên suốt ứng dụng.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String requestId = RequestUtils.extractRequestId();
   * System.out.println(requestId); // "1234567890"
   * }
   * </pre>
   *
   * @return Giá trị `requestId` hoặc `null` nếu không có.
   */
  public static String extractRequestId() {
    return ThreadContext.get(RequestConstant.REQUEST_ID);
  }

  /**
   * **Lấy `sessionId` từ `ThreadContext` (`extractSessionId`)**
   *
   * <p>Phương thức này lấy `sessionId` đã được gán vào `ThreadContext`,
   * giúp theo dõi phiên làm việc của người dùng.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String sessionId = RequestUtils.extractSessionId();
   * System.out.println(sessionId); // "ABCDEF123456"
   * }
   * </pre>
   *
   * @return Giá trị `sessionId` hoặc `null` nếu không có.
   */
  public static String extractSessionId() {
    return ThreadContext.get(RequestConstant.SESSION_ID);
  }

  /**
   * **Lấy ngôn ngữ của client từ `ThreadContext` (`extractLocale`)**
   *
   * <p>Phương thức này lấy thông tin ngôn ngữ từ `ThreadContext` và
   * trả về đối tượng `Locale` tương ứng.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Kiểm tra giá trị `CLIENT_LANG` trong `ThreadContext`.
   * - Nếu không có, trả về `Locale.ENGLISH`.
   * - Nếu có, tạo `Locale` mới với mã ngôn ngữ đó.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Locale locale = RequestUtils.extractLocale();
   * System.out.println(locale.getLanguage()); // "vi", "en", "fr", ...
   * }
   * </pre>
   *
   * @return Đối tượng `Locale` xác định ngôn ngữ của client.
   */
  public static Locale extractLocale() {
    var lang = ThreadContext.get(RequestConstant.CLIENT_LANG);
    return lang == null ? Locale.ENGLISH : Locale.of(lang);
  }
}
