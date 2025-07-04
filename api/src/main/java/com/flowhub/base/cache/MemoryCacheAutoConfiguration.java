package com.flowhub.base.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * **Cấu hình bộ nhớ đệm với Caffeine**
 *
 * <p>Class này cấu hình bộ nhớ đệm (cache) sử dụng **Caffeine**. Nó chỉ được kích hoạt khi
 * thuộc tính `custom.properties.cache.type` được thiết lập là `"caffeine"`.</p>
 *
 * <h2>Chức năng chính:</h2>
 * <ul>
 *   <li>✅ Cấu hình `Ticker` để đo thời gian hết hạn của cache.</li>
 *   <li>✅ Cung cấp `CacheManager` để quản lý danh sách các bộ nhớ đệm.</li>
 *   <li>✅ Xây dựng cache với thời gian hết hạn và kích thước tối đa do người dùng cấu hình.</li>
 * </ul>
 *
 * <h2>Điều kiện kích hoạt:</h2>
 * <p>Class này chỉ được kích hoạt khi thuộc tính sau tồn tại trong cấu hình:</p>
 * <pre>
 * {@code
 * custom.properties.cache.type=caffeine
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "custom.properties.cache.type", havingValue = "caffeine")
@Import(CacheAutoConfiguration.class)
public class MemoryCacheAutoConfiguration {

  /**
   * **Ticker hệ thống để đo thời gian trong cache**
   *
   * <p>Bean này cung cấp một ticker hệ thống dùng để đo thời gian hết hạn của bộ nhớ đệm.</p>
   *
   * @return một đối tượng {@code Ticker} hệ thống
   */
  @Bean
  public Ticker ticker() {
    return Ticker.systemTicker();
  }

  /**
   * **CacheManager để quản lý bộ nhớ đệm**
   *
   * <p>Bean này cung cấp một `CacheManager`, chịu trách nhiệm quản lý tất cả các bộ nhớ đệm trong
   * hệ thống. Nếu không có `CacheManager` nào khác tồn tại, Spring sẽ sử dụng bean này.</p>
   *
   * <h2>Điều kiện tạo bean này:</h2>
   * <ul>
   *   <li>📌 Không có `CacheManager` nào khác được khai báo.</li>
   *   <li>📌 `custom.properties.cache.type` phải có giá trị `"caffeine"`.</li>
   * </ul>
   *
   * @param ticker          Ticker hệ thống dùng để đo thời gian hết hạn
   * @param cacheProperties Cấu hình bộ nhớ đệm được lấy từ file cấu hình
   * @return một {@code CacheManager} quản lý danh sách cache
   */
  @Bean("memoryCacheManager")
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "custom.properties.cache.type", havingValue = "caffeine")
  public CacheManager cacheManager(final Ticker ticker, final CacheProperties cacheProperties) {
    final List<CaffeineCache> caches = new ArrayList<>();
    cacheProperties
        .getProperties()
        .forEach(
            (k, v) -> {
              CaffeineCache cache = this.buildCache(v, ticker);
              caches.add(cache);
            });
    final SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(caches);
    return manager;
  }

  /**
   * **Tạo một bộ nhớ đệm Caffeine**
   *
   * <p>Phương thức này tạo một cache với các tham số được cấu hình bởi người dùng, bao gồm:</p>
   * <ul>
   *   <li>⏳ **Thời gian hết hạn** của cache sau khi ghi.</li>
   *   <li>📦 **Kích thước tối đa** của cache.</li>
   *   <li>🕰️ **Ticker hệ thống** để đo thời gian.</li>
   * </ul>
   *
   * @param cacheBuilder Cấu hình bộ nhớ đệm do người dùng chỉ định
   * @param ticker       Ticker hệ thống dùng để đo thời gian hết hạn
   * @return một instance của {@code CaffeineCache}
   */
  private CaffeineCache buildCache(final CacheBuilder cacheBuilder, final Ticker ticker) {
    return new CaffeineCache(
        cacheBuilder.getCacheName(),
        Caffeine.newBuilder()
                .expireAfterWrite(cacheBuilder.getExpiredTime())
                .maximumSize(cacheBuilder.getMaximumSize())
                .ticker(ticker)
                .build());
  }
}
