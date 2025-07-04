package com.flowhub.base.config;

import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * **Cấu hình nguồn tài nguyên ngôn ngữ (`MessageSourceConfig`)**
 *
 * <p>Lớp này cấu hình `MessageSource` để hỗ trợ **đa ngôn ngữ (i18n)** trong ứng dụng.
 * Nó giúp nạp các tệp tin chứa thông báo (`messages.properties`) từ thư mục tài nguyên.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Cung cấp `MessageSource` để Spring Boot có thể sử dụng khi cần dịch thông báo.</li>
 *   <li>✅ Hỗ trợ tự động tải lại file thông báo mà không cần khởi động lại ứng dụng.</li>
 *   <li>✅ Thiết lập bảng mã UTF-8 để đảm bảo hiển thị đúng tiếng Việt và các ngôn ngữ khác.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Lớp này sử dụng `ReloadableResourceBundleMessageSource` để nạp thông báo từ thư mục `lang/messages`
 * bên trong `classpath`. Điều này có nghĩa là các file `messages.properties` có thể được đặt trong
 * `src/main/resources/lang/` và Spring Boot sẽ tự động sử dụng chúng.</p>
 * <p>
 * **📌 Ví dụ cấu trúc thư mục chứa file ngôn ngữ:**
 * <pre>
 * src/main/resources/lang/
 * ├── messages.properties        (Mặc định, dùng khi không có ngôn ngữ cụ thể)
 * ├── messages_vi.properties     (Tiếng Việt)
 * ├── messages_en.properties     (Tiếng Anh)
 * ├── messages_ja.properties     (Tiếng Nhật)
 * </pre>
 * <p>
 * **📌 Ví dụ nội dung file `messages_vi.properties`:**
 * <pre>
 * greeting=Chào mừng bạn đến với ứng dụng!
 * error.not_found=Không tìm thấy dữ liệu yêu cầu.
 * </pre>
 * <p>
 * **📌 Sử dụng trong Controller hoặc Service:**
 * <p>Spring Boot sẽ tự động lấy thông báo tương ứng dựa trên ngôn ngữ của yêu cầu.</p>
 * <pre>
 * {@code
 * @Autowired
 * private MessageSource messageSource;
 *
 * public String getWelcomeMessage(Locale locale) {
 *     return messageSource.getMessage("greeting", null, locale);
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
public class MessageSourceConfig {

  /**
   * **Đường dẫn mặc định đến file chứa thông báo**
   *
   * <p>Biến này xác định vị trí của các tệp tin chứa thông báo trong thư mục tài nguyên
   * (`resources/lang/messages`).</p>
   */
  private static final String DEFAULT_BUNDLE_PATH = "classpath:lang/messages";

  /**
   * **Cấu hình `MessageSource` để hỗ trợ đa ngôn ngữ**
   *
   * <p>Phương thức này tạo một `ReloadableResourceBundleMessageSource` giúp Spring Boot có thể
   * tải các file `messages.properties` từ thư mục tài nguyên (`resources/lang`).</p>
   * <p>
   * **Cấu hình bao gồm:**
   * <ul>
   *   <li>📌 Thiết lập `basename` để xác định vị trí chứa file thông báo.</li>
   *   <li>📌 Định dạng mã hóa UTF-8 để hỗ trợ tiếng Việt và các ngôn ngữ khác.</li>
   *   <li>📌 Cho phép tự động tải lại file thông báo mà không cần khởi động lại ứng dụng.</li>
   * </ul>
   *
   * @return Đối tượng `ReloadableResourceBundleMessageSource` đã được cấu hình.
   */
  @Bean
  public ReloadableResourceBundleMessageSource messageSource() {
    var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames(DEFAULT_BUNDLE_PATH);
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    return messageSource;
  }
}
