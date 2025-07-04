package com.flowhub.base.cache;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;

/**
 * **Lớp cấu hình bộ nhớ đệm (`CacheBuilder`)**
 *
 * <p>Đây là lớp dùng để thiết lập cấu hình cho bộ nhớ đệm (cache).</p>
 *
 * <h2>📌 Chức năng chính:</h2>
 * <ul>
 *   <li>✅ Xác định tên của cache (`cacheName`).</li>
 *   <li>✅ Thiết lập thời gian hết hạn của cache (`expiredTime`).</li>
 *   <li>✅ Xác định số lượng phần tử tối đa mà cache có thể chứa (`maximumSize`).</li>
 * </ul>
 *
 * <h2>📌 Cách hoạt động:</h2>
 * <p>Lớp này chỉ chứa các thuộc tính và phương thức getter/setter, không có bất kỳ logic xử lý nào.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * CacheBuilder cacheBuilder = new CacheBuilder();
 * cacheBuilder.setCacheName("userCache");
 * cacheBuilder.setExpiredTime(Duration.ofMinutes(10));
 * cacheBuilder.setMaximumSize(1000);
 *
 * System.out.println("Tên cache: " + cacheBuilder.getCacheName());
 * System.out.println("Thời gian hết hạn: " + cacheBuilder.getExpiredTime());
 * System.out.println("Kích thước tối đa: " + cacheBuilder.getMaximumSize());
 * }
 * </pre>
 *
 * <h2>Chi tiết các trường trong lớp:</h2>
 * <ul>
 *   <li>🔹 `cacheName` - Tên bộ nhớ đệm, giúp xác định cache trong hệ thống.</li>
 *   <li>🔹 `expiredTime` - Khoảng thời gian trước khi một mục trong cache bị xóa tự động.</li>
 *   <li>🔹 `maximumSize` - Số lượng phần tử tối đa trong cache (mặc định là `Integer.MAX_VALUE`).</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class CacheBuilder {

  /**
   * **Tên của cache**
   *
   * <p>Thuộc tính này giúp xác định cache trong hệ thống. Mỗi cache nên có một tên duy nhất để
   * tránh xung đột khi quản lý nhiều cache khác nhau.</p>
   */
  private String cacheName;

  /**
   * **Thời gian hết hạn của cache**
   *
   * <p>Đây là khoảng thời gian mà một mục trong cache sẽ tồn tại trước khi bị tự động xóa.
   * Nếu giá trị này không được đặt, cache sẽ không có thời gian hết hạn.</p>
   */
  private Duration expiredTime;

  /**
   * **Số lượng phần tử tối đa trong cache**
   *
   * <p>Giới hạn số lượng phần tử có thể lưu trữ trong cache. Nếu số lượng này bị vượt quá,
   * cache sẽ tự động loại bỏ các mục cũ hơn để nhường chỗ cho các mục mới.</p>
   *
   * <p>Mặc định giá trị này được đặt là `Integer.MAX_VALUE`, có nghĩa là cache có thể chứa
   * tối đa số phần tử bằng giá trị lớn nhất của một số nguyên (`2^31 - 1`).</p>
   */
  private int maximumSize = Integer.MAX_VALUE;
}
