package com.flowhub.base.data;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.flowhub.base.annotations.SnowflakeGeneratedId;


/**
 * **Lớp cơ sở cho các entity (`BaseEntity`)**
 *
 * <p>Lớp này cung cấp các trường chung được sử dụng trong tất cả các entity của hệ thống.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <p>Trong một entity cụ thể, chỉ cần kế thừa `BaseEntity` để tự động có các trường chung.</p>
 * <p>
 * **📌 Ví dụ entity kế thừa `BaseEntity`:**
 * <pre>
 * {@code
 * @Entity
 * public class User extends BaseEntity {
 *     private String username;
 *     private String email;
 * }
 * }
 * </pre>
 * <p>
 * **📌 Cơ chế Auditing tự động hoạt động khi có cấu hình `@EnableJpaAuditing` trong
 * `PersistenceConfig.java`.**
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FieldNameConstants
public abstract class BaseEntity implements Serializable {

  /**
   * **Serial Version UID** Dùng để đảm bảo tính tương thích khi serialize đối tượng.
   */
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * **Khóa chính của entity (`ID`)**
   *
   * <p>Sử dụng `@GeneratedValue` kết hợp với `SnowflakeGeneratorStrategy` để tạo ID duy nhất.</p>
   */
  @Id
  @Column(name = "ID")
  @GeneratedValue
  @SnowflakeGeneratedId
  private Long id;

  /**
   * **Thời gian tạo bản ghi (`CREATED_AT`)**
   *
   * <p>Trường này sẽ tự động được gán giá trị khi bản ghi được tạo.</p>
   */
  @CreatedDate
  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * **Thời gian cập nhật bản ghi (`UPDATED_AT`)**
   *
   * <p>Trường này sẽ tự động được cập nhật mỗi khi bản ghi bị chỉnh sửa.</p>
   */
  @LastModifiedDate
  @Column(name = "UPDATED_AT", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * **Người tạo bản ghi (`CREATED_BY`)**
   *
   * <p>Lưu lại ID hoặc username của người đã tạo bản ghi.</p>
   */
  @CreatedBy
  @Column(name = "CREATED_BY", nullable = false, updatable = false)
  private String createdBy;

  /**
   * **Người chỉnh sửa gần nhất (`UPDATED_BY`)**
   *
   * <p>Lưu lại ID hoặc username của người chỉnh sửa lần cuối.</p>
   */
  @LastModifiedBy
  @Column(name = "UPDATED_BY", nullable = false)
  private String updatedBy;

  /**
   * **Cờ đánh dấu bản ghi đã bị xóa (`IS_DELETED`)**
   *
   * <p>Trường này cho phép "xóa mềm" (soft delete). Thay vì xóa dữ liệu trong DB, ta chỉ đánh dấu
   * nó là `true`.</p>
   */
  @Column(name = "IS_DELETED", nullable = false)
  private boolean deleted = false;

  /**
   * **Cơ chế kiểm soát phiên bản (`VERSION`)**
   *
   * <p>Giúp quản lý xung đột khi có nhiều người dùng cùng cập nhật bản ghi.</p>
   */
  @Version
  @Column(name = "VERSION", nullable = false)
  private Long version;
}
