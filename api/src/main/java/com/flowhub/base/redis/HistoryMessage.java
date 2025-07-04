package com.flowhub.base.redis;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * **Lớp `HistoryMessage` - Lưu trữ thông tin về tin nhắn đã được xử lý**
 *
 * <p>Đây là một class POJO đơn giản, dùng để lưu trữ thông tin về tin nhắn
 * đã được xử lý trong hệ thống, bao gồm ID tin nhắn, điểm đến, loại broker, và thời gian tiêu thụ
 * tin nhắn.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * HistoryMessage message = new HistoryMessage("msg-123", "queue-1", "Kafka");
 * System.out.println(message.getConsumeAt()); // 2025-02-14T10:00:00
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class HistoryMessage implements Serializable {

  /** ID của tin nhắn** */
  private String messageId;

  /** Điểm đến của tin nhắn (ví dụ: tên queue, topic, endpoint API, v.v.)** */
  private String destination;

  /** Loại broker sử dụng để gửi tin nhắn (ví dụ: Kafka, RabbitMQ, ActiveMQ, v.v.)** */
  private String brokerType;

  /** Thời gian tin nhắn được tiêu thụ trong hệ thống** */
  private LocalDateTime consumeAt;

  /**
   * **Constructor khởi tạo tin nhắn (`HistoryMessage`)**
   *
   * <p>Phương thức này tạo một đối tượng `HistoryMessage` mới với ID tin nhắn,
   * điểm đến, loại broker, và tự động đặt thời gian tiêu thụ (`consumeAt`) là thời điểm hiện
   * tại.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * HistoryMessage message = new HistoryMessage("msg-001", "queue-orders", "Kafka");
   * System.out.println(message.getConsumeAt()); // Thời gian hiện tại
   * }
   * </pre>
   *
   * @param messageId   ID của tin nhắn.
   * @param destination Điểm đến của tin nhắn (queue, topic, API, v.v.).
   * @param brokerType  Loại broker sử dụng (Kafka, RabbitMQ, v.v.).
   */
  public HistoryMessage(String messageId, String destination, String brokerType) {
    this.messageId = messageId;
    this.consumeAt = LocalDateTime.now();
    this.destination = destination;
    this.brokerType = brokerType;
  }
}
