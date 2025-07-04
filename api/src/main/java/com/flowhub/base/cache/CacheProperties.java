package com.flowhub.base.cache;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * **Cấu hình các thuộc tính bộ nhớ đệm (CacheProperties)**
 *
 * <p>Lớp này được sử dụng để cấu hình các thuộc tính của bộ nhớ đệm trong ứng dụng.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Xác định loại bộ nhớ đệm được sử dụng (`type`).</li>
 *   <li>✅ Quản lý danh sách cấu hình bộ nhớ đệm cho từng cache cụ thể (`properties`).</li>
 *   <li>✅ Tự động ánh xạ các thuộc tính từ file cấu hình bằng annotation `@ConfigurationProperties`.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Annotation `@ConfigurationProperties(prefix = "custom.properties.cache")` giúp ánh xạ dữ liệu
 * từ file cấu hình vào các thuộc tính trong lớp này. Ví dụ:</p>
 * <pre>
 * {@code
 * custom.properties.cache.type=caffeine
 * custom.properties.cache.properties.userCache.expiredTime=600
 * custom.properties.cache.properties.userCache.maximumSize=1000
 * }
 * </pre>
 * <p>Các giá trị trên sẽ được ánh xạ vào các trường của lớp này.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <p>Lớp này có thể được sử dụng trong một cấu hình `CacheManager` như sau:</p>
 * <pre>
 * {@code
 * @Autowired
 * private CacheProperties cacheProperties;
 *
 * public void setupCache() {
 *     CacheType cacheType = cacheProperties.getType();
 *     Map<String, CacheBuilder> cacheConfig = cacheProperties.getProperties();
 * }
 * }
 * </pre>
 *
 * <h2>Chi tiết các trường trong lớp:</h2>
 * <ul>
 *   <li>🔹 `type` - Kiểu cache được sử dụng (ví dụ: Caffeine, Redis, Simple, None).</li>
 *   <li>🔹 `properties` - Danh sách cấu hình cho từng cache cụ thể, ánh xạ từ file cấu hình.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "custom.properties.cache")
public class CacheProperties {

  /**
   * **Loại bộ nhớ đệm (Cache Type)**
   *
   * <p>Trường này xác định loại cache mà ứng dụng sẽ sử dụng, chẳng hạn như:</p>
   * <ul>
   *   <li>📌 `CacheType.CAFFEINE` - Sử dụng bộ nhớ đệm Caffeine.</li>
   *   <li>📌 `CacheType.REDIS` - Sử dụng Redis để lưu trữ cache phân tán.</li>
   *   <li>📌 `CacheType.SIMPLE` - Sử dụng bộ nhớ đệm đơn giản trong bộ nhớ (in-memory).</li>
   *   <li>📌 `CacheType.NONE` - Không sử dụng bộ nhớ đệm.</li>
   * </ul>
   */
  private CacheType type;

  /**
   * **Danh sách cấu hình cho từng cache cụ thể**
   *
   * <p>Trường này là một `Map` trong đó:</p>
   * <ul>
   *   <li>🔹 **Key** - Tên của cache (ví dụ: `"userCache"`, `"productCache"`).</li>
   *   <li>🔹 **Value** - Đối tượng `CacheBuilder`, chứa thông tin cấu hình như **thời gian hết hạn**,
   *   **kích thước tối đa**, và các thông số khác.</li>
   * </ul>
   *
   * <h3>Ví dụ cấu hình trong file `application.yml`:</h3>
   * <pre>
   * {@code
   * custom:
   *   properties:
   *     cache:
   *       type: caffeine
   *       properties:
   *         userCache:
   *           expiredTime: 600
   *           maximumSize: 1000
   * }
   * </pre>
   */
  private Map<String, CacheBuilder> properties;
}
