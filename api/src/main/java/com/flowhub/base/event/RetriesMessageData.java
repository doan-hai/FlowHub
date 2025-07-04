package com.flowhub.base.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `RetriesMessageData` - Quản lý tin nhắn retry trong hệ thống**
 *
 * <p>Lớp này được sử dụng để lưu trữ và quản lý thông tin về các tin nhắn cần retry
 * trong hệ thống message queue, giúp đảm bảo tin nhắn được xử lý đúng cách khi gặp lỗi.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một tin nhắn không thể xử lý thành công, hệ thống sẽ tạo một bản ghi retry
 * và lưu vào hàng đợi để thử lại sau một khoảng thời gian (`delayTime`).</p>
 * <p>
 * **📌 Ví dụ sử dụng `RetriesMessageData` trong hệ thống retry:**
 * <pre>
 * {@code
 * RetriesMessageData<String> retryMessage = new RetriesMessageData<>(
 *     "retry-123",
 *     new MessageData<>("Thông báo lỗi", "Nội dung tin nhắn"),
 *     "retry.topic",
 *     5000L, // Delay 5 giây
 *     3      // Số lần thử lại
 * );
 * System.out.println("Message sẽ được retry vào: " + retryMessage.getPreExecuteAt());
 * }
 * </pre>
 *
 * @param <T> Kiểu dữ liệu của nội dung tin nhắn.
 * @author haidv
 * @version 1.0
 */
@Getter
@Setter
public class RetriesMessageData<T> {

  /** ID duy nhất của tin nhắn retry (sinh bằng Snowflake)** */
  private String messageId;

  /** ID của tin nhắn gốc cần retry** */
  private String originMessageId;

  /** Dữ liệu tin nhắn cần retry** */
  private MessageData<T> data;

  /** Tên Kafka topic chứa tin nhắn cần retry** */
  private String topic;

  /** Nguồn phát sinh tin nhắn (service name hoặc module name)** */
  private String source;

  /** Đích của tin nhắn sau khi retry (thường là topic Kafka đích)** */
  private String destination;

  /** Số lần thử lại của tin nhắn này** */
  private Integer retriesNo;

  /** Số lần tối đa tin nhắn có thể thử lại** */
  private Integer repeatCount;

  /** Thời gian delay trước khi thử lại (tính bằng milliseconds)** */
  private Long delayTime;

  /** Thời gian thực thi thử lại tiếp theo** */
  private LocalDateTime preExecuteAt;

  /** Trạng thái của tin nhắn retry (`INSERT`, `DELETE`, `UPDATE`)** */
  private RetriesMessageDataStatus status;

  /**
   * **Constructor mặc định**
   *
   * <p>Khởi tạo một tin nhắn retry mới với ID tự động sinh, trạng thái mặc định là `INSERT`,
   * và số lần thử lại ban đầu là `1`.</p>
   */
  public RetriesMessageData() {
    this.messageId = String.valueOf(Snowflake.getInstance().nextId());
    this.retriesNo = 1;
    this.status = RetriesMessageDataStatus.INSERT;
  }

  /**
   * **Constructor tạo tin nhắn retry với thông tin đầy đủ**
   *
   * <p>Sử dụng khi cần khởi tạo một tin nhắn retry với dữ liệu cụ thể,
   * bao gồm ID tin nhắn gốc, dữ liệu tin nhắn, topic, delay time, và số lần thử lại.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * RetriesMessageData<String> retryMessage = new RetriesMessageData<>(
   *     "retry-123",
   *     new MessageData<>("Thông báo lỗi", "Nội dung tin nhắn"),
   *     "retry.topic",
   *     5000L,
   *     3
   * );
   * }
   * </pre>
   *
   * @param originMessageId ID của tin nhắn gốc cần retry.
   * @param data            Nội dung tin nhắn retry.
   * @param topic           Kafka topic cần gửi lại tin nhắn.
   * @param delayTime       Thời gian delay trước khi thử lại (milliseconds).
   * @param repeatCount     Số lần thử lại tối đa.
   */
  public RetriesMessageData(
      String originMessageId,
      MessageData<T> data,
      String topic,
      long delayTime,
      Integer repeatCount) {
    this();
    this.originMessageId = originMessageId;
    this.data = data;
    this.topic = topic;
    this.delayTime = delayTime;
    this.repeatCount = repeatCount;
    this.preExecuteAt = LocalDateTime.now();
  }

  /**
   * **Tăng số lần thử lại (`incrementRetriesNo`)**
   *
   * <p>Phương thức này tạo một bản ghi retry mới với số lần thử lại tăng lên 1.
   * Nó cũng cập nhật thời gian thực thi tiếp theo và đặt trạng thái `UPDATE`.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * retryMessage.incrementRetriesNo();
   * System.out.println("Số lần retry hiện tại: " + retryMessage.getRetriesNo());
   * }
   * </pre>
   *
   * @return Đối tượng `RetriesMessageData` đã được cập nhật.
   */
  public RetriesMessageData<T> incrementRetriesNo() {
    this.messageId = String.valueOf(Snowflake.getInstance().nextId());
    this.retriesNo = this.retriesNo + 1;
    this.data = null;
    this.source = null;
    this.destination = null;
    this.topic = null;
    this.repeatCount = null;
    this.preExecuteAt = LocalDateTime.now();
    this.status = RetriesMessageDataStatus.UPDATE;
    return this;
  }

  /**
   * **Xóa tin nhắn retry (`deleteRetries`)**
   *
   * <p>Đánh dấu tin nhắn retry là đã xóa bằng cách đặt trạng thái `DELETE`.
   * Cũng xóa các thông tin không cần thiết để giảm dung lượng lưu trữ.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * retryMessage.deleteRetries();
   * System.out.println("Trạng thái tin nhắn: " + retryMessage.getStatus());
   * }
   * </pre>
   *
   * @return Đối tượng `RetriesMessageData` đã được cập nhật.
   */
  public RetriesMessageData<T> deleteRetries() {
    this.messageId = String.valueOf(Snowflake.getInstance().nextId());
    this.retriesNo = null;
    this.data = null;
    this.source = null;
    this.destination = null;
    this.topic = null;
    this.delayTime = null;
    this.repeatCount = null;
    this.status = RetriesMessageDataStatus.DELETE;
    return this;
  }

  /**
   * **Enum `RetriesMessageDataStatus` - Trạng thái của tin nhắn retry**
   *
   * <ul>
   *   <li>📌 `INSERT`: Tin nhắn vừa được tạo và cần đưa vào hàng đợi retry.</li>
   *   <li>📌 `DELETE`: Tin nhắn không còn cần retry và sẽ bị xóa.</li>
   *   <li>📌 `UPDATE`: Tin nhắn đã được thử lại và cần cập nhật trạng thái.</li>
   * </ul>
   */
  public enum RetriesMessageDataStatus {
    INSERT,
    DELETE,
    UPDATE
  }
}