package com.flowhub.base.event;

/**
 * **Giao diện `EventData` - Định nghĩa dữ liệu của một sự kiện**
 *
 * <p>Giao diện này được sử dụng để đánh dấu các lớp dữ liệu đi kèm với sự kiện trong hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các lớp chứa dữ liệu sự kiện cần triển khai `EventData` để xác định
 * chúng thuộc hệ thống sự kiện.</p>
 * <p>
 * **📌 Ví dụ triển khai `EventData`:**
 * <pre>
 * {@code
 * public class UserCreatedEventData implements EventData {
 *     private Long userId;
 *     private String username;
 *
 *     public UserCreatedEventData(Long userId, String username) {
 *         this.userId = userId;
 *         this.username = username;
 *     }
 *
 *     public Long getUserId() { return userId; }
 *     public String getUsername() { return username; }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ứng dụng thực tế:**
 * <ul>
 *   <li>📌 Dùng trong hệ thống event-driven để truyền dữ liệu kèm theo sự kiện.</li>
 *   <li>📌 Giúp tách biệt dữ liệu sự kiện với logic xử lý sự kiện.</li>
 *   <li>📌 Dễ dàng mở rộng bằng cách tạo các lớp dữ liệu mới.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
public interface EventData {

}
