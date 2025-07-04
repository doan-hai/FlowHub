package com.flowhub.base.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `EventInfo` - Lưu trữ thông tin về một sự kiện**
 *
 * <p>Lớp này được sử dụng để đóng gói thông tin về một sự kiện trong hệ thống event-driven.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi sự kiện khi được tạo sẽ được gán một ID duy nhất (sinh từ thuật toán Snowflake)
 * và có thể được xử lý đồng bộ hoặc bất đồng bộ, tùy thuộc vào cấu hình.</p>
 * <p>
 * **📌 Ví dụ sử dụng `EventInfo` trong một sự kiện người dùng được tạo:**
 * <pre>
 * {@code
 * EventData eventData = new UserCreatedEventData(123L, "john_doe");
 * CoreEvent coreEvent = new UserCreatedEvent();
 * EventInfo eventInfo = new EventInfo(eventData, coreEvent, false, 3);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public class EventInfo {

  /** Số lần thử lại mặc định khi xử lý sự kiện** */
  public static final int DEFAULT_RETRY_EVENT = 3;

  /** ID duy nhất của sự kiện (được sinh bằng Snowflake)** */
  private final String id;

  /** Dữ liệu của sự kiện (thực thể triển khai `EventData`)** */
  private final EventData what;

  /** Đối tượng sự kiện (thực thể triển khai `CoreEvent`)** */
  private final CoreEvent event;

  /** Xác định sự kiện có được xử lý đồng bộ (`true`) hay bất đồng bộ (`false`)** */
  private final boolean isSync;

  /** Số lần thử lại khi sự kiện gặp lỗi** */
  private final int retry;

  /**
   * **Constructor tạo sự kiện với số lần thử lại mặc định**
   *
   * <p>Gán `retry` = `DEFAULT_RETRY_EVENT` (3 lần).</p>
   *
   * @param what  Dữ liệu sự kiện.
   * @param event Đối tượng sự kiện.
   */
  public EventInfo(EventData what, CoreEvent event) {
    this(what, event, DEFAULT_RETRY_EVENT);
  }

  /**
   * **Constructor tạo sự kiện với số lần thử lại tùy chỉnh**
   *
   * @param what  Dữ liệu sự kiện.
   * @param event Đối tượng sự kiện.
   * @param retry Số lần thử lại khi xử lý sự kiện gặp lỗi.
   */
  public EventInfo(EventData what, CoreEvent event, int retry) {
    this(what, false, event, retry);
  }

  /**
   * **Constructor tạo sự kiện đồng bộ hoặc bất đồng bộ**
   *
   * <p>Mặc định số lần thử lại là `DEFAULT_RETRY_EVENT` (3 lần).</p>
   *
   * @param what   Dữ liệu sự kiện.
   * @param isSync `true` nếu xử lý đồng bộ, `false` nếu xử lý bất đồng bộ.
   * @param event  Đối tượng sự kiện.
   */
  public EventInfo(EventData what, boolean isSync, CoreEvent event) {
    this(what, isSync, event, DEFAULT_RETRY_EVENT);
  }

  /**
   * **Constructor đầy đủ cho `EventInfo`**
   *
   * <p>Sinh ID duy nhất bằng thuật toán Snowflake.</p>
   *
   * @param what   Dữ liệu sự kiện.
   * @param isSync `true` nếu xử lý đồng bộ, `false` nếu xử lý bất đồng bộ.
   * @param event  Đối tượng sự kiện.
   * @param retry  Số lần thử lại khi xử lý sự kiện gặp lỗi.
   */
  public EventInfo(EventData what, boolean isSync, CoreEvent event, int retry) {
    this(String.valueOf(Snowflake.getInstance().nextId()), what, event, isSync, retry);
  }
}
