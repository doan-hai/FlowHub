package com.flowhub.base.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `MessageData` - Định dạng dữ liệu tin nhắn sự kiện**
 *
 * <p>Lớp này được sử dụng để đóng gói thông tin của một tin nhắn hoặc dữ liệu
 * sự kiện trong hệ thống, giúp truyền tải dữ liệu một cách nhất quán.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một tin nhắn được khởi tạo mà không có `messageId`, hệ thống sẽ tự động
 * sinh ID duy nhất bằng thuật toán **Snowflake** để đảm bảo không trùng lặp.</p>
 * <p>
 * **📌 Ví dụ sử dụng `MessageData` trong hệ thống sự kiện:**
 * <pre>
 * {@code
 * MessageData<String> message = new MessageData<>("Thông báo hệ thống", "Nội dung tin nhắn");
 * System.out.println("Message ID: " + message.getMessageId());
 * }
 * </pre>
 *
 * @param <T> Kiểu dữ liệu của nội dung tin nhắn.
 * @author haidv
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class MessageData<T> {

  /** ID duy nhất của tin nhắn** */
  private String messageId;

  /** Tiêu đề hoặc chủ đề của tin nhắn** */
  private String subject;

  /** Nội dung tin nhắn (có thể là bất kỳ kiểu dữ liệu nào)** */
  private T content;

  /**
   * **Constructor tạo tin nhắn với nội dung (`content`)**
   *
   * <p>Hệ thống sẽ tự động sinh `messageId` bằng thuật toán Snowflake.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * MessageData<String> message = new MessageData<>("Nội dung tin nhắn");
   * System.out.println("Message ID: " + message.getMessageId());
   * }
   * </pre>
   *
   * @param content Nội dung tin nhắn.
   */
  public MessageData(T content) {
    this.messageId = String.valueOf(Snowflake.getInstance().nextId());
    this.content = content;
  }

  /**
   * **Constructor tạo tin nhắn với chủ đề (`subject`) và nội dung (`content`)**
   *
   * <p>Hệ thống sẽ tự động sinh `messageId` bằng thuật toán Snowflake.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * MessageData<String> message = new MessageData<>("Thông báo", "Nội dung tin nhắn");
   * System.out.println("Message Subject: " + message.getSubject());
   * }
   * </pre>
   *
   * @param subject Chủ đề hoặc tiêu đề của tin nhắn.
   * @param content Nội dung tin nhắn.
   */
  public MessageData(String subject, T content) {
    this(content);
    this.subject = subject;
  }

  /**
   * **Cập nhật ID của tin nhắn (`updateMessageId`)**
   *
   * <p>Cho phép thay đổi `messageId` của tin nhắn khi cần thiết.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * MessageData<String> message = new MessageData<>("Nội dung ban đầu");
   * message.updateMessageId("123456789");
   * System.out.println("Updated Message ID: " + message.getMessageId());
   * }
   * </pre>
   *
   * @param messageId ID mới của tin nhắn.
   */
  public void updateMessageId(String messageId) {
    this.messageId = messageId;
  }
}
