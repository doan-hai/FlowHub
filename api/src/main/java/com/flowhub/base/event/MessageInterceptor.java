package com.flowhub.base.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.flowhub.base.utils.JsonUtils;
import com.flowhub.business.BusinessApplication;

/**
 * **Lớp `MessageInterceptor` - Gửi tin nhắn tới Kafka topic**
 *
 * <p>Lớp này chịu trách nhiệm chuyển đổi và gửi dữ liệu tới các hàng đợi Kafka.
 * Nó hỗ trợ gửi tin nhắn thông qua các phương thức khác nhau, có thể gửi kèm theo khóa
 * (`key`).</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi khi cần gửi một tin nhắn tới Kafka, hệ thống sẽ gọi một trong các phương thức
 * `convertAndSend()`, sau đó tin nhắn sẽ được chuyển đổi thành JSON và gửi vào topic Kafka tương
 * ứng.</p>
 * <p>
 * **📌 Ví dụ sử dụng `MessageInterceptor`:**
 * <pre>
 * {@code
 * @Component
 * public class NotificationService {
 *     private final MessageInterceptor messageInterceptor;
 *
 *     public NotificationService(MessageInterceptor messageInterceptor) {
 *         this.messageInterceptor = messageInterceptor;
 *     }
 *
 *     public void sendNotification() {
 *         MessageData<String> message = new MessageData<>("Thông báo", "Nội dung tin nhắn");
 *         messageInterceptor.convertAndSend("notification.topic", message);
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageInterceptor {

  /** Chuỗi log khi bắt đầu gửi tin nhắn** */
  private static final String LOG_START =
      "Start push message to queue: {} messageId: {} with payload: {}";

  /** Chuỗi log khi kết thúc gửi tin nhắn** */
  private static final String LOG_END = "End push message to queue: {} messageId: {}";

  /** Đối tượng KafkaTemplate dùng để gửi tin nhắn tới Kafka** */
  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  /** Tên topic Kafka cho các sự kiện retry (lấy từ file cấu hình)** */
  @Value("${custom.properties.kafka.topic.retries-event.name}")
  private String retriesEventTopic;

  /**
   * **Gửi sự kiện retry (`convertAndSendRetriesEvent`)**
   *
   * <p>Phương thức này gửi một sự kiện `RetriesMessageData` vào topic retry
   * để xử lý lại khi có lỗi xảy ra.</p>
   * <p>
   * **📌 Cách xử lý:**
   * <ul>
   *   <li>📌 Ghi log trước khi gửi tin nhắn.</li>
   *   <li>📌 Nếu trạng thái tin nhắn là `INSERT`, cập nhật thông tin nguồn (`source`)
   *   và điểm đến (`destination`).</li>
   *   <li>📌 Chuyển tin nhắn thành JSON và gửi vào Kafka topic.</li>
   *   <li>📌 Ghi log sau khi gửi.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * RetriesMessageData retryMessage = new RetriesMessageData();
   * retryMessage.setMessageId("123456");
   * retryMessage.setStatus(RetriesMessageData.RetriesMessageDataStatus.INSERT);
   * messageInterceptor.convertAndSendRetriesEvent(retryMessage);
   * }
   * </pre>
   *
   * @param payload Dữ liệu sự kiện retry cần gửi.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void convertAndSendRetriesEvent(RetriesMessageData payload) {
    log.info(LOG_START, retriesEventTopic, payload.getMessageId(), JsonUtils.toJson(payload));
    if (payload.getStatus().equals(RetriesMessageData.RetriesMessageDataStatus.INSERT)) {
      payload.setSource(BusinessApplication.getApplicationName().toUpperCase());
      payload.setDestination(
          String.format("%s.%s.%s", payload.getTopic(), payload.getSource(), "RETRIES"));
    }
    kafkaTemplate.send(retriesEventTopic, payload.getMessageId(), JsonUtils.toJson(payload));
    log.info(LOG_END, retriesEventTopic, payload.getMessageId());
  }

  /**
   * **Gửi tin nhắn tới Kafka topic (`convertAndSend`)**
   *
   * <p>Phương thức này gửi một tin nhắn `MessageData` vào một Kafka topic cụ thể.</p>
   * <p>
   * **📌 Cách xử lý:**
   * <ul>
   *   <li>📌 Ghi log trước khi gửi tin nhắn.</li>
   *   <li>📌 Chuyển tin nhắn thành JSON.</li>
   *   <li>📌 Gửi tin nhắn vào Kafka topic.</li>
   *   <li>📌 Ghi log sau khi gửi.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * MessageData<String> message = new MessageData<>("Thông báo", "Nội dung tin nhắn");
   * messageInterceptor.convertAndSend("notification.topic", message);
   * }
   * </pre>
   *
   * @param queueName Tên topic Kafka.
   * @param payload   Dữ liệu tin nhắn cần gửi.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void convertAndSend(String queueName, MessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    log.info(LOG_START, queueName, payload.getMessageId(), payloadJson);
    kafkaTemplate.send(queueName, payloadJson);
    log.info(LOG_END, queueName, payload.getMessageId());
  }

  /**
   * **Gửi tin nhắn với khóa (`convertAndSend`)**
   *
   * <p>Phương thức này gửi tin nhắn tới một Kafka topic với khóa (`key`) để đảm bảo dữ liệu
   * được phân bổ đúng vào các phân vùng Kafka.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * MessageData<String> message = new MessageData<>("Thông báo", "Nội dung tin nhắn");
   * messageInterceptor.convertAndSend("notification.topic", "user123", message);
   * }
   * </pre>
   *
   * @param queueName Tên topic Kafka.
   * @param key       Khóa để phân vùng tin nhắn trong Kafka.
   * @param payload   Dữ liệu tin nhắn cần gửi.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void convertAndSend(String queueName, String key, MessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    log.info(LOG_START, queueName, payload.getMessageId(), payloadJson);
    kafkaTemplate.send(queueName, key, payloadJson);
    log.info(LOG_END, queueName, payload.getMessageId());
  }

  /**
   * **Gửi tin nhắn retry (`convertAndSend`)**
   *
   * <p>Phương thức này gửi tin nhắn `RetriesMessageData` tới Kafka khi cần xử lý lại sự kiện.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * RetriesMessageData retryMessage = new RetriesMessageData();
   * retryMessage.setDestination("retry.topic");
   * retryMessage.setMessageId("123456");
   * messageInterceptor.convertAndSend(retryMessage);
   * }
   * </pre>
   *
   * @param payload Dữ liệu tin nhắn retry cần gửi.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void convertAndSend(RetriesMessageData payload) {
    var payloadJson = JsonUtils.toJson(payload);
    log.info(LOG_START, payload.getDestination(), payload.getMessageId(), payloadJson);
    kafkaTemplate.send(payload.getDestination(), payload.getOriginMessageId(), payloadJson);
    log.info(LOG_END, payload.getDestination(), payload.getMessageId());
  }
}