package com.flowhub.base.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import static org.springframework.http.converter.StringHttpMessageConverter.DEFAULT_CHARSET;

/**
 * **Interceptor cho HTTP Requests trong RestTemplate (`CustomClientHttpRequestInterceptor`)**
 *
 * <p>Lớp này được sử dụng để **chặn và ghi log chi tiết** các yêu cầu (request) và phản hồi
 * (response) khi sử dụng `RestTemplate` trong Spring.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Chặn và ghi log các yêu cầu HTTP trước khi gửi đi.</li>
 *   <li>✅ Chặn và ghi log phản hồi từ server sau khi nhận được.</li>
 *   <li>✅ Hỗ trợ lấy `charset` từ tiêu đề phản hồi để đảm bảo dữ liệu được xử lý đúng.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Interceptor này sẽ được thêm vào `RestTemplate` và tự động ghi lại thông tin các request/response.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong cấu hình `RestTemplate`:**
 * <pre>
 * {@code
 * @Bean
 * public RestTemplate restTemplate() {
 *     RestTemplate restTemplate = new RestTemplate();
 *     restTemplate.getInterceptors().add(new CustomClientHttpRequestInterceptor());
 *     return restTemplate;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

  /**
   * **Phương thức chặn và ghi log yêu cầu, phản hồi HTTP**
   *
   * <p>Phương thức này sẽ được gọi mỗi khi một yêu cầu HTTP được gửi đi. Nó sẽ:</p>
   * <ul>
   *   <li>📌 Ghi log chi tiết yêu cầu trước khi gửi (`traceRequest`).</li>
   *   <li>📌 Thực hiện yêu cầu và nhận phản hồi từ server.</li>
   *   <li>📌 Ghi log chi tiết phản hồi từ server (`traceResponse`).</li>
   * </ul>
   *
   * @param request   Đối tượng `HttpRequest` chứa thông tin yêu cầu HTTP.
   * @param body      Dữ liệu gửi đi trong yêu cầu (body request).
   * @param execution Đối tượng `ClientHttpRequestExecution` dùng để thực hiện yêu cầu.
   * @return Phản hồi `ClientHttpResponse` từ server.
   * @throws IOException Nếu xảy ra lỗi trong quá trình gửi hoặc nhận dữ liệu.
   */
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    traceRequest(request, body);
    var response = execution.execute(request, body);
    traceResponse(response, request);
    return response;
  }


  /**
   * **Ghi log chi tiết yêu cầu HTTP**
   *
   * <p>Phương thức này sẽ ghi log thông tin của yêu cầu trước khi gửi, bao gồm:</p>
   * <ul>
   *   <li>📌 URI của yêu cầu.</li>
   *   <li>📌 Phương thức HTTP (`GET`, `POST`, ...).</li>
   *   <li>📌 Tiêu đề HTTP (Headers).</li>
   *   <li>📌 Nội dung (body) của yêu cầu.</li>
   * </ul>
   *
   * @param request Đối tượng `HttpRequest` chứa thông tin yêu cầu.
   * @param body    Nội dung của yêu cầu dưới dạng mảng byte.
   */
  private void traceRequest(HttpRequest request, byte[] body) {
    var str = new String(body, StandardCharsets.UTF_8);
    log.info(
        "RestTemplate request logging: URI: {} Method: {} Headers: {} Body: {}",
        request.getURI(),
        request.getMethod(),
        request.getHeaders(),
        str);
  }

  /**
   * **Ghi log chi tiết phản hồi HTTP**
   *
   * <p>Phương thức này sẽ ghi log thông tin phản hồi từ server, bao gồm:</p>
   * <ul>
   *   <li>📌 URI của yêu cầu.</li>
   *   <li>📌 Phương thức HTTP (`GET`, `POST`, ...).</li>
   *   <li>📌 Mã trạng thái HTTP (`200 OK`, `404 Not Found`, ...).</li>
   *   <li>📌 Tiêu đề HTTP (Headers).</li>
   *   <li>📌 Nội dung phản hồi (body).</li>
   * </ul>
   *
   * @param response Đối tượng `ClientHttpResponse` chứa thông tin phản hồi.
   * @param request  Đối tượng `HttpRequest` tương ứng với yêu cầu đã gửi đi.
   * @throws IOException Nếu xảy ra lỗi trong quá trình đọc dữ liệu phản hồi.
   */
  private void traceResponse(ClientHttpResponse response, HttpRequest request) throws IOException {
    var str = new String(StreamUtils.copyToByteArray(response.getBody()), getCharset(response));
    log.info(
        "RestTemplate response logging: URI: {} Method: {} Status code: {} Headers: {} Body: {}",
        request.getURI(),
        request.getMethod(),
        response.getStatusCode(),
        response.getHeaders(),
        str);
  }

  /**
   * **Lấy `charset` từ tiêu đề phản hồi HTTP**
   *
   * <p>Phương thức này giúp xác định bộ mã hóa (`Charset`) của phản hồi từ server.</p>
   * <ul>
   *   <li>📌 Nếu phản hồi có tiêu đề `Content-Type` chứa `charset`, phương thức sẽ trả về bộ mã hóa đó.</li>
   *   <li>📌 Nếu không có `charset`, phương thức sẽ sử dụng giá trị mặc định (`UTF-8`).</li>
   * </ul>
   *
   * @param message Đối tượng `HttpMessage` chứa tiêu đề HTTP của phản hồi.
   * @return Bộ mã hóa (`Charset`) được sử dụng để đọc nội dung phản hồi.
   */
  private Charset getCharset(HttpMessage message) {
    return Optional.ofNullable(message.getHeaders().getContentType())
                   .map(MediaType::getCharset)
                   .orElse(DEFAULT_CHARSET);
  }
}
