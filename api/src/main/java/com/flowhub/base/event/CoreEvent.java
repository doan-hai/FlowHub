package com.flowhub.base.event;

/**
 * **Giao diện `CoreEvent` - Định nghĩa sự kiện trong hệ thống**
 *
 * <p>Giao diện này cung cấp các phương thức cần thiết để xác định một sự kiện trong hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các lớp triển khai `CoreEvent` cần cung cấp thông tin về sự kiện, bao gồm:</p>
 * <ul>
 *   <li>📌 `getEventName()`: Tên của sự kiện.</li>
 *   <li>📌 `getHandleEventBeanName()`: Tên của bean xử lý sự kiện.</li>
 *   <li>📌 `getHandleEventFunctionName()`: Tên phương thức trong bean sẽ xử lý sự kiện.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ triển khai `CoreEvent`:**
 * <pre>
 * {@code
 * public class UserCreatedEvent implements CoreEvent {
 *
 *     @Override
 *     public String getEventName() {
 *         return "UserCreated";
 *     }
 *
 *     @Override
 *     public String getHandleEventBeanName() {
 *         return "userEventHandler";
 *     }
 *
 *     @Override
 *     public String getHandleEventFunctionName() {
 *         return "handleUserCreated";
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ứng dụng thực tế:**
 * <ul>
 *   <li>📌 Định nghĩa sự kiện trong hệ thống theo mô hình event-driven.</li>
 *   <li>📌 Cho phép ánh xạ sự kiện với một bean cụ thể để xử lý.</li>
 *   <li>📌 Giúp tách biệt logic sự kiện khỏi logic nghiệp vụ chính.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
public interface CoreEvent {

  /**
   * **Lấy tên của sự kiện (`getEventName`)**
   *
   * <p>Tên này được sử dụng để xác định loại sự kiện đang diễn ra trong hệ thống.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String eventName = event.getEventName(); // "UserCreated"
   * }
   * </pre>
   *
   * @return Tên sự kiện.
   */
  String getEventName();

  /**
   * **Lấy tên bean xử lý sự kiện (`getHandleEventBeanName`)**
   *
   * <p>Tên bean này sẽ được sử dụng để tìm kiếm một bean Spring cụ thể để xử lý sự kiện.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String beanName = event.getHandleEventBeanName(); // "userEventHandler"
   * }
   * </pre>
   *
   * @return Tên bean xử lý sự kiện.
   */
  String getHandleEventBeanName();

  /**
   * **Lấy tên phương thức xử lý sự kiện (`getHandleEventFunctionName`)**
   *
   * <p>Phương thức này trả về tên của phương thức sẽ được gọi trong bean xử lý sự kiện.</p>
   * <p>
   * **📌 Ví dụ:**
   * <pre>
   * {@code
   * String functionName = event.getHandleEventFunctionName(); // "handleUserCreated"
   * }
   * </pre>
   *
   * @return Tên phương thức xử lý sự kiện.
   */
  String getHandleEventFunctionName();
}
