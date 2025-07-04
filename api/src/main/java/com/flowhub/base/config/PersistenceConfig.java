package com.flowhub.base.config;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import com.flowhub.base.constant.RequestConstant;

/**
 * **Cấu hình Persistence (`PersistenceConfig`)**
 *
 * <p>Lớp này cung cấp các cấu hình cần thiết để hỗ trợ **JPA Auditing** trong ứng dụng.
 * Nó giúp tự động ghi lại thông tin về **người thực hiện** và **thời điểm thực hiện** khi các
 * entity được tạo hoặc cập nhật.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Cấu hình `DateTimeProvider` để tự động lấy thời gian hiện tại khi auditing.</li>
 *   <li>✅ Cấu hình `AuditorAware` để xác định người thực hiện thao tác trong hệ thống.</li>
 *   <li>✅ Kích hoạt JPA Auditing bằng annotation `@EnableJpaAuditing`.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Spring Data JPA sử dụng `AuditorAware` và `DateTimeProvider` để tự động
 * điền thông tin **người thực hiện** (`createdBy`, `lastModifiedBy`) và **thời gian** (`createdDate`, `lastModifiedDate`)
 * khi thao tác với cơ sở dữ liệu.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong Entity:**
 * <pre>
 * {@code
 * @Entity
 * @EntityListeners(AuditingEntityListener.class)
 * public class User {
 *
 *     @CreatedBy
 *     private String createdBy;
 *
 *     @CreatedDate
 *     private LocalDateTime createdDate;
 *
 *     @LastModifiedBy
 *     private String lastModifiedBy;
 *
 *     @LastModifiedDate
 *     private LocalDateTime lastModifiedDate;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing(
    auditorAwareRef = "auditorProvider",
    dateTimeProviderRef = "auditingDateTimeProvider")
public class PersistenceConfig {

  /**
   * **Cung cấp thời gian hiện tại cho JPA Auditing**
   *
   * <p>Phương thức này tạo một `DateTimeProvider` giúp tự động lấy thời gian hiện tại
   * khi auditing trong cơ sở dữ liệu.</p>
   *
   * @return Đối tượng `DateTimeProvider` cung cấp `LocalDateTime.now()`.
   */
  @Bean(name = "auditingDateTimeProvider")
  public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(LocalDateTime.now());
  }

  /**
   * **Cung cấp thông tin người thực hiện thao tác (Auditor)**
   *
   * <p>Phương thức này trả về một `AuditorAware<String>`, giúp Spring Security lấy
   * thông tin người dùng hiện tại để lưu vào các trường `createdBy`, `lastModifiedBy`.</p>
   *
   * @return Đối tượng `AuditorAware` lấy thông tin người dùng từ `SecurityContextHolder`.
   */
  @Bean
  AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  /**
   * **Lớp triển khai `AuditorAware` để lấy thông tin người thực hiện**
   *
   * <p>Lớp này thực hiện phương thức `getCurrentAuditor()`, sử dụng Spring Security để
   * lấy thông tin người dùng hiện tại từ `SecurityContextHolder`.</p>
   */
  public static class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * **Lấy ID của người dùng hiện tại**
     *
     * <p>Phương thức này sẽ lấy tên của người dùng hiện tại từ `SecurityContextHolder`.
     * Nếu không có người dùng nào đăng nhập, nó sẽ trả về một giá trị mặc định (`SYSTEM`).</p>
     *
     * @return `Optional<String>` chứa ID của người thực hiện thao tác.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
      try {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userId == null || userId.equals("anonymousUser")
            ? Optional.of(RequestConstant.SYSTEM)
            : Optional.of(userId);
      } catch (Exception e) {
        return Optional.of(RequestConstant.SYSTEM);
      }
    }
  }
}
