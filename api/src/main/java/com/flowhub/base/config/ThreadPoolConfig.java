package com.flowhub.base.config;

import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * **Cấu hình Thread Pool cho xử lý tác vụ bất đồng bộ (`ThreadPoolConfig`)**
 *
 * <p>Lớp này cung cấp cấu hình cho `ThreadPoolTaskExecutor`, một cơ chế quản lý luồng
 * giúp thực thi các tác vụ bất đồng bộ hiệu quả trong Spring.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Định nghĩa một `ThreadPoolTaskExecutor` với các thông số được thiết lập từ file cấu hình.</li>
 *   <li>✅ Hỗ trợ thực thi các tác vụ một cách tối ưu bằng cách quản lý luồng một cách linh hoạt.</li>
 *   <li>✅ Sử dụng chính sách `CallerRunsPolicy` để xử lý các tác vụ bị từ chối khi hàng đợi đầy.</li>
 *   <li>✅ Chỉ kích hoạt khi thuộc tính `custom.properties.service.thread.pool.task.executor=true`.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Thread Pool giúp giới hạn số luồng chạy đồng thời, cải thiện hiệu suất hệ thống
 * bằng cách tái sử dụng luồng thay vì tạo mới liên tục.</p>
 * <p>
 * **📌 Cấu hình trong `application.yml`:**
 * <pre>
 * {@code
 * custom:
 *   properties:
 *     service:
 *       thread:
 *         pool:
 *           task:
 *             executor: true
 *             core:
 *               pool:
 *                 size: 10
 *             max:
 *               pool:
 *                 size: 50
 *             queue:
 *               capacity: 100
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ sử dụng `@Async` với Thread Pool:**
 * <pre>
 * {@code
 * @Service
 * public class AsyncService {
 *
 *     @Async
 *     public void executeTask() {
 *         System.out.println("Thực hiện tác vụ trên luồng: " + Thread.currentThread().getName());
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@ConditionalOnProperty(
    value = "custom.properties.service.thread.pool.task.executor",
    havingValue = "true")
@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

  /**
   * **Số luồng lõi của Thread Pool**
   *
   * <p>Giá trị này xác định số luồng tối thiểu luôn hoạt động trong Thread Pool.</p>
   */
  @Value("${custom.properties.service.thread.pool.task.executor.core.pool.size}")
  private int corePoolSize;

  /**
   * **Số luồng tối đa của Thread Pool**
   *
   * <p>Giá trị này xác định số luồng tối đa có thể tạo ra trong Thread Pool
   * khi tải công việc tăng cao.</p>
   */
  @Value("${custom.properties.service.thread.pool.task.executor.max.pool.size}")
  private int maxPoolSize;

  /**
   * **Dung lượng hàng đợi của Thread Pool**
   *
   * <p>Hàng đợi lưu trữ các tác vụ chờ xử lý khi tất cả các luồng đang bận.</p>
   */
  @Value("${custom.properties.service.thread.pool.task.executor.queue.capacity}")
  private int queueCapacity;

  /**
   * **Cấu hình `ThreadPoolTaskExecutor`**
   *
   * <p>Phương thức này khởi tạo và cấu hình `ThreadPoolTaskExecutor` với các tham số sau:</p>
   * <ul>
   *   <li>📌 `corePoolSize` - Số luồng lõi hoạt động liên tục.</li>
   *   <li>📌 `maxPoolSize` - Số luồng tối đa có thể mở rộng khi tải cao.</li>
   *   <li>📌 `queueCapacity` - Kích thước hàng đợi chứa tác vụ chờ xử lý.</li>
   *   <li>📌 `ThreadNamePrefix` - Tiền tố đặt tên luồng để dễ dàng theo dõi.</li>
   *   <li>📌 `CallerRunsPolicy` - Chính sách xử lý khi hàng đợi đầy, sẽ thực hiện tác vụ trên luồng gọi.</li>
   * </ul>
   *
   * @return Một `ThreadPoolTaskExecutor` đã được cấu hình.
   */
  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("service-task_executor-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
