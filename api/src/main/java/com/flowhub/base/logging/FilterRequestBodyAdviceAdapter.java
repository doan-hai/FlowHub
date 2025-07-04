package com.flowhub.base.logging;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import com.flowhub.base.data.EncryptInput;
import com.flowhub.base.data.SecureInput;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;
import com.flowhub.base.security.AbstractSecurityRequest;
import com.flowhub.base.utils.JsonUtils;

import static com.flowhub.base.logging.LoggingUtil.logRequest;

/**
 * **Lớp `FilterRequestBodyAdviceAdapter` - Xử lý dữ liệu đầu vào của request body**
 *
 * <p>Đây là một `RequestBodyAdviceAdapter` giúp xử lý dữ liệu đầu vào trong request body,
 * đặc biệt là các request có dữ liệu được mã hóa hoặc bảo mật.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi khi một request gửi dữ liệu lên server, lớp này sẽ kiểm tra xem request
 * có chứa dữ liệu được mã hóa (`EncryptInput`) hoặc có chữ ký bảo mật (`SecureInput`). Nếu có, nó
 * sẽ thực hiện các thao tác giải mã hoặc kiểm tra chữ ký trước khi chuyển tiếp request.</p>
 * <pre>
 * **📌 Ví dụ dữ liệu mã hóa (`EncryptInput`):**
 * ```json
 * {
 *   "encryptData": "H4sh3dV4lu3",
 *   "rawData": null
 * }
 * ```
 *
 * **📌 Ví dụ dữ liệu có chữ ký (`SecureInput`):**
 * ```json
 * {
 *   "data": { "username": "user123", "timestamp": 1700000000 },
 *   "signature": "abc123xyz"
 * }
 * ```
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class FilterRequestBodyAdviceAdapter extends RequestBodyAdviceAdapter {

  /** Request hiện tại, dùng để lấy thông tin request header** */
  private final HttpServletRequest httpServletRequest;

  /** Cấu hình logging để xác định có ghi log request hay không** */
  private final LoggingProperties loggingProperties;

  /** Application context để lấy bean xử lý bảo mật** */
  private final ApplicationContext applicationContext;

  /** Tên bean dùng để giải mã dữ liệu trong request** */
  @Value("${custom.properties.security.request.security-bean-name}")
  private String secureRequestBeanName;

  /**
   * **Xác định request có cần xử lý bởi `RequestBodyAdvice` không (`supports`)**
   *
   * <p>Phương thức này quyết định liệu `RequestBodyAdviceAdapter` có cần can thiệp
   * vào việc xử lý request hay không. Ở đây, nó luôn trả về `true` để áp dụng cho tất cả
   * request.</p>
   *
   * @param methodParameter Thông tin về phương thức xử lý request.
   * @param targetType      Kiểu dữ liệu của body trong request.
   * @param converterType   Kiểu `HttpMessageConverter` đang được sử dụng.
   * @return Luôn trả về `true`, nghĩa là áp dụng cho tất cả request.
   */
  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  /**
   * **Xử lý dữ liệu trong request body sau khi đọc (`afterBodyRead`)**
   *
   * <p>Phương thức này xử lý request body sau khi đã được đọc, bao gồm:</p>
   * <ul>
   *   <li>📌 Nếu request chứa `EncryptInput`, giải mã dữ liệu.</li>
   *   <li>📌 Nếu request chứa `SecureInput`, kiểm tra chữ ký dữ liệu.</li>
   *   <li>📌 Ghi log request để hỗ trợ theo dõi.</li>
   * </ul>
   *
   * @param body          Dữ liệu của request body.
   * @param inputMessage  Đối tượng `HttpInputMessage` chứa request gốc.
   * @param parameter     Thông tin về phương thức xử lý request.
   * @param targetType    Kiểu dữ liệu của body.
   * @param converterType Kiểu `HttpMessageConverter` đang được sử dụng.
   * @return Dữ liệu đã được xử lý, có thể là bản gốc hoặc đã được giải mã/xác thực.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {

    // **📌 Xử lý request có EncryptInput (dữ liệu mã hóa)**
    switch (body) {
      case EncryptInput secureRequest -> {
        secureRequest.setRawData(null); // Xóa dữ liệu thô trước khi giải mã

        try {
          // **Lấy bean xử lý bảo mật để giải mã dữ liệu**
          var json =
              applicationContext
                  .getAutowireCapableBeanFactory()
                  .getBean(secureRequestBeanName, AbstractSecurityRequest.class)
                  .decryptData(secureRequest.getEncryptData());

          // **Chuyển đổi dữ liệu JSON thành object tương ứng**
          secureRequest.setRawData(
              JsonUtils.fromJson(
                  json, (Class) ((ParameterizedType) targetType).getActualTypeArguments()[0]));

        } catch (Exception e) {
          log.error(e.getMessage(), e);
          throw new BaseException(CommonErrorDef.NOT_DECRYPT_SECURE_REQUEST);
        } finally {
          // **Ghi log request để giám sát**
          logRequest(httpServletRequest, loggingProperties, secureRequest);
        }

        return super.afterBodyRead(secureRequest,
                                   inputMessage,
                                   parameter,
                                   targetType,
                                   converterType);
      }

      // **📌 Xử lý request có SecureInput (dữ liệu có chữ ký bảo mật)**
      case SecureInput secureRequest -> {
        logRequest(httpServletRequest, loggingProperties, secureRequest);

        // **Lấy khóa để kiểm tra tính hợp lệ của chữ ký**
        var checksumKey =
            applicationContext
                .getAutowireCapableBeanFactory()
                .getBean(secureRequestBeanName, AbstractSecurityRequest.class)
                .getChecksumKey();

        // **Nếu dữ liệu không hợp lệ, ném ngoại lệ**
        if (!secureRequest.isValidData(checksumKey)) {
          throw new BaseException(CommonErrorDef.SIGNATURE_INVALID);
        }

        return super.afterBodyRead(secureRequest,
                                   inputMessage,
                                   parameter,
                                   targetType,
                                   converterType);
      }

      // **📌 Xử lý request thông thường (không mã hóa, không có chữ ký)**
      default -> {
        logRequest(httpServletRequest, loggingProperties, body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
      }
    }
  }
}