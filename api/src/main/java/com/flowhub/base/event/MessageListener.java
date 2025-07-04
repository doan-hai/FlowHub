package com.flowhub.base.event;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.ParameterizedType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.redis.HistoryMessage;
import com.flowhub.base.redis.HistoryMessageRepository;
import com.flowhub.base.utils.JsonUtils;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `MessageListener` - Lắng nghe và xử lý tin nhắn từ Kafka**
 *
 * <p>Lớp này là một `abstract class` dùng để xử lý tin nhắn Kafka. Các lớp con cần kế thừa
 * và triển khai phương thức `handleMessageEvent()` để xử lý nội dung tin nhắn.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi Kafka gửi một tin nhắn đến, `MessageListener` sẽ:</p>
 * <ul>
 *   <li>📌 Ghi log thông tin tin nhắn.</li>
 *   <li>📌 Kiểm tra xem tin nhắn đã được xử lý chưa thông qua Redis.</li>
 *   <li>📌 Gọi `handleMessageEvent()` để xử lý nội dung tin nhắn.</li>
 *   <li>📌 Nếu gặp lỗi, gửi tin nhắn vào hàng đợi retry để thử lại.</li>
 *   <li>📌 Cuối cùng, xác nhận Kafka đã nhận tin nhắn (`acknowledgment.acknowledge()`).</li>
 * </ul>
 * <p>
 * **📌 Ví dụ triển khai một lớp con từ `MessageListener`:**
 * <pre>
 * {@code
 * @Component
 * public class UserMessageListener extends MessageListener<UserData> {
 *
 *     @Override
 *     protected void handleMessageEvent(String topic, String partition, String offset, MessageData<UserData> input) {
 *         System.out.println("Received user data: " + input.getContent());
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> Kiểu dữ liệu của tin nhắn.
 * @author haidv
 * @version 1.0
 */
@Slf4j
public abstract class MessageListener<T> {

  /** Kho lưu trữ lịch sử tin nhắn trong Redis để tránh xử lý trùng lặp** */
  protected HistoryMessageRepository historyMessageRepository;

  /** Interceptor để gửi tin nhắn retry khi có lỗi xảy ra** */
  protected MessageInterceptor messageInterceptor;

  /**
   * **Inject `MessageInterceptor` để hỗ trợ gửi tin nhắn retry**
   *
   * @param messageInterceptor Đối tượng `MessageInterceptor`.
   */
  @Autowired
  public final void setMessageInterceptor(MessageInterceptor messageInterceptor) {
    this.messageInterceptor = messageInterceptor;
  }

  /**
   * **Inject `HistoryMessageRepository` để kiểm tra tin nhắn đã xử lý**
   *
   * @param historyMessageRepository Kho lưu trữ tin nhắn đã xử lý.
   */
  @Autowired
  public final void setHistoryMessageRepository(HistoryMessageRepository historyMessageRepository) {
    this.historyMessageRepository = historyMessageRepository;
  }

  /**
   * **Kiểm tra kiểu dữ liệu generic được hỗ trợ**
   *
   * <p>Phương thức này được gọi sau khi bean được khởi tạo để đảm bảo lớp con
   * có kiểu dữ liệu `T` hợp lệ.</p>
   */
  @PostConstruct
  public void checkGenericTypeSupport() {
    try {
      this.getMessageContentType();
    } catch (Exception e) {
      log.error("MessageListener generic type support failed.");
      throw e;
    }
  }

  /**
   * **Lắng nghe và xử lý tin nhắn từ Kafka (`messageListener`)**
   *
   * <p>Phiên bản này chỉ truyền tham số cơ bản, không có delay và retry.</p>
   *
   * @param data           Dữ liệu tin nhắn từ Kafka.
   * @param topic          Tên Kafka topic.
   * @param partition      Phân vùng Kafka.
   * @param offset         Vị trí tin nhắn trong Kafka.
   * @param acknowledgment Đối tượng xác nhận tin nhắn đã nhận.
   */
  @SuppressWarnings("unused")
  public void messageListener(
      String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
    messageListener(data, topic, partition, offset, acknowledgment, 0, 0);
  }

  /**
   * **Lắng nghe và xử lý tin nhắn từ Kafka (`messageListener`)**
   *
   * <p>Phiên bản này hỗ trợ retry và delay nếu gặp lỗi.</p>
   *
   * @param data           Dữ liệu tin nhắn từ Kafka.
   * @param topic          Tên Kafka topic.
   * @param partition      Phân vùng Kafka.
   * @param offset         Vị trí tin nhắn trong Kafka.
   * @param acknowledgment Đối tượng xác nhận tin nhắn đã nhận.
   * @param delayTime      Thời gian delay nếu cần retry.
   * @param repeatCount    Số lần thử lại khi gặp lỗi.
   */
  public void messageListener(
      String data,
      String topic,
      String partition,
      String offset,
      Acknowledgment acknowledgment,
      long delayTime,
      Integer repeatCount) {
    this.initListener(topic, partition, offset, data);
    MessageData<T> input =
        JsonUtils.fromJson(data, MessageData.class, this.getMessageContentType());
    if (isNotValidMessage(input, topic, partition, offset, acknowledgment)) {
      return;
    }

    if (StringUtils.isBlank(input.getMessageId())) {
      input.updateMessageId(String.format("%s_%s_%s", topic, partition, offset));
    }
    try {
      if (isDuplicateMessage(input.getMessageId(), topic)) {
        return;
      }
      this.handleMessageEvent(topic, partition, offset, input);
      log.info("[KafkaConsumer][{}][{}][{}] Processed successfully!", topic, partition, offset);
    } catch (Exception e) {
      log.error("[KafkaConsumer][{}][{}][{}] Exception occurred: ", topic, partition, offset, e);
      handleRetry(input, topic, delayTime, repeatCount);
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }

  /**
   * **Xử lý tin nhắn retry**
   */
  public void messageRetriesListener(
      String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
    this.initListener(topic, partition, offset, data);
    RetriesMessageData<T> retryMessage =
        JsonUtils.fromJson(data, RetriesMessageData.class, this.getMessageContentType());
    if (isNotValidMessage(retryMessage, topic, partition, offset, acknowledgment)) {
      return;
    }
    MessageData<T> retryData = retryMessage.getData();
    if (isNotValidMessage(retryData, topic, partition, offset, acknowledgment)) {
      return;
    }
    try {
      if (isDuplicateMessage(retryMessage.getMessageId(), topic)) {
        return;
      }
      handleMessageEvent(topic, partition, offset, retryData);
      messageInterceptor.convertAndSendRetriesEvent(retryMessage.deleteRetries());
      log.info("[KafkaConsumer][{}][{}][{}] Retries processed successfully!",
               topic,
               partition,
               offset);
    } catch (Exception e) {
      log.error("[KafkaConsumer][{}][{}][{}] Retry failed", topic, partition, offset, e);
      messageInterceptor.convertAndSendRetriesEvent(retryMessage.incrementRetriesNo());
    } finally {
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
    }
  }

  /**
   * **Kiểm tra tin nhắn có hợp lệ không**
   */
  private boolean isNotValidMessage(Object input, String topic, String partition, String offset,
                                    Acknowledgment acknowledgment) {
    if (input == null) {
      log.info("[KafkaConsumer][{}][{}][{}] Ignored invalid message", topic, partition, offset);
      acknowledgment.acknowledge();
      ThreadContext.clearAll();
      return true;
    }
    return false;
  }

  /**
   * **Kiểm tra tin nhắn đã được xử lý chưa**
   */
  private boolean isDuplicateMessage(String messageId, String topic) {
    if (Boolean.FALSE.equals(historyMessageRepository.put(new HistoryMessage(messageId,
                                                                             topic,
                                                                             RequestConstant.BROKER_KAFKA)))) {
      log.warn("[KafkaConsumer][{}] Message already processed", topic);
      return true;
    }
    return false;
  }

  /**
   * **Xử lý retry khi gặp lỗi**
   */
  private void handleRetry(MessageData<T> input, String topic, long delayTime,
                           Integer repeatCount) {
    if (repeatCount > 0) {
      messageInterceptor.convertAndSendRetriesEvent(new RetriesMessageData<>(input.getMessageId(),
                                                                             input,
                                                                             topic,
                                                                             delayTime,
                                                                             repeatCount));
    }
  }

  /**
   * **Khởi tạo thông tin log khi lắng nghe tin nhắn**
   *
   * @param topic     Tên Kafka topic.
   * @param partition Phân vùng Kafka.
   * @param offset    Vị trí tin nhắn trong Kafka.
   * @param data      Dữ liệu tin nhắn.
   */
  private void initListener(String topic, String partition, String offset, String data) {
    ThreadContext.put(RequestConstant.REQUEST_ID, String.valueOf(Snowflake.getInstance().nextId()));
    ThreadContext.put(RequestConstant.BROKER_TYPE, RequestConstant.BROKER_KAFKA);
    ThreadContext.put(RequestConstant.MESSAGE_EVENT, topic);
    log.info("[KafkaConsumer][{}][{}][{}] Incoming: {}", topic, partition, offset, data);
  }

  /**
   * **Phương thức trừu tượng để xử lý nội dung tin nhắn**
   *
   * <p>Các lớp con phải triển khai phương thức này để xử lý tin nhắn theo yêu cầu.</p>
   *
   * @param topic     Tên Kafka topic.
   * @param partition Phân vùng Kafka.
   * @param offset    Vị trí tin nhắn trong Kafka.
   * @param input     Nội dung tin nhắn cần xử lý.
   */
  protected abstract void handleMessageEvent(
      String topic, String partition, String offset, MessageData<T> input);

  /**
   * **Lấy kiểu dữ liệu của tin nhắn (`T`)**
   *
   * @return Kiểu dữ liệu của tin nhắn.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private Class getMessageContentType() {
    return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }
}