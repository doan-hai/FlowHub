package com.flowhub.base.data;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import com.flowhub.base.utils.Snowflake;

/**
 * **Chiến lược sinh ID bằng Snowflake (`SnowflakeGeneratorStrategy`)**
 *
 * <p>Lớp này triển khai `IdentifierGenerator` của Hibernate để tạo ID duy nhất
 * sử dụng thuật toán **Snowflake ID**.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Trong quá trình lưu entity vào cơ sở dữ liệu, nếu entity chưa có ID,
 * lớp này sẽ tự động tạo một ID mới sử dụng Snowflake và gán vào entity.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong entity:**
 * <pre>
 * {@code
 * @Entity
 * public class User extends BaseEntity {
 *     @Id
 *     @GeneratedValue(generator = "snowflake")
 *     @GenericGenerator(name = "snowflake", type = SnowflakeGeneratorStrategy.class)
 *     private Long id;
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public class SnowflakeGeneratorStrategy implements IdentifierGenerator {

  /** Loại dữ liệu của ID (hỗ trợ Long)** */
  private AttributeType idType;

  /**
   * **Cấu hình kiểu dữ liệu ID (`configure`)**
   *
   * <p>Phương thức này được gọi khi Hibernate khởi tạo generator, giúp xác định
   * kiểu dữ liệu của ID (`Long`).</p>
   *
   * @param type            Kiểu dữ liệu của ID (Hibernate Type).
   * @param params          Các tham số cấu hình từ Hibernate.
   * @param serviceRegistry Đối tượng `ServiceRegistry` của Hibernate.
   * @throws MappingException Nếu kiểu dữ liệu ID không được hỗ trợ.
   */
  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    idType = AttributeType.valueOf(type.getReturnedClass());
  }

  /**
   * **Sinh ID cho entity (`generate`)**
   *
   * <p>Phương thức này được Hibernate gọi mỗi khi cần tạo ID cho một entity.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Nếu entity đã có ID (khác `null`), trả về ID cũ.</li>
   *   <li>📌 Nếu entity chưa có ID, sinh một ID mới bằng **Snowflake**.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ hoạt động:**
   * <pre>
   * {@code
   * Long id = (Long) generator.generate(session, new User());
   * System.out.println("Generated ID: " + id);
   * }
   * </pre>
   *
   * @param session Đối tượng `SharedSessionContractImplementor` của Hibernate.
   * @param object  Đối tượng entity cần tạo ID.
   * @return ID được tạo mới hoặc ID đã có của entity.
   * @throws HibernateException Nếu có lỗi trong quá trình sinh ID.
   */
  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    if (object instanceof BaseEntity obj && obj.getId() != null) {
      return idType.cast(obj.getId());
    }
    return idType.cast(Snowflake.getInstance().nextId());
  }

  /**
   * **Enum xác định kiểu dữ liệu của ID (`AttributeType`)**
   *
   * <p>Hiện tại chỉ hỗ trợ kiểu `Long`, nhưng có thể mở rộng cho các kiểu khác nếu cần.</p>
   */
  public enum AttributeType {

    /** Kiểu dữ liệu Long** */
    LONG {
      @Override
      public Serializable cast(Long id) {
        return id;
      }
    };

    /**
     * **Xác định kiểu dữ liệu ID từ lớp Java (`valueOf`)**
     *
     * <p>Hàm này kiểm tra xem kiểu dữ liệu của ID có hợp lệ không.</p>
     *
     * @param clazz Lớp của thuộc tính ID.
     * @return Kiểu dữ liệu tương ứng.
     * @throws HibernateException Nếu kiểu dữ liệu không được hỗ trợ.
     */
    static AttributeType valueOf(Class<?> clazz) {
      if (Long.class.isAssignableFrom(clazz)) {
        return LONG;
      } else {
        throw new HibernateException(
            String.format(
                "The @Tsid annotation on [%s] can only be placed on a Long or String entity attribute!",
                clazz));
      }
    }

    /**
     * **Chuyển đổi ID về kiểu dữ liệu phù hợp (`cast`)**
     *
     * @param id ID cần chuyển đổi.
     * @return ID đã được ép kiểu.
     */
    public abstract Serializable cast(Long id);
  }
}
