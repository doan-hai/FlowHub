package com.flowhub.base.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import com.flowhub.business.BusinessApplication;

/**
 * **Lớp `HistoryMessageRepository` - Lưu trữ tin nhắn vào Redis**
 *
 * <p>Repository này sử dụng Redis để lưu trữ thông tin về các tin nhắn đã được xử lý,
 * giúp kiểm tra và ngăn chặn việc xử lý trùng lặp.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Sử dụng `RedisTemplate<String, Object>` để thao tác với Redis.
 * - Lưu trữ tin nhắn dưới dạng key-value.
 * - Key được tạo từ thông tin broker, ứng dụng, điểm đến và ID tin nhắn.
 * - Tin nhắn được lưu với TTL (Time To Live) là 12 giờ.
 * </pre>
 * **📌 Ví dụ key lưu trữ trong Redis:** ```
 * LOYALTY_HISTORY_MESSAGE:KAFKA:BASE_PROJECT:QUEUE_ORDERS:MSG_12345 ```
 *
 * @author haidv
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class HistoryMessageRepository {

  /** Đối tượng RedisTemplate để thao tác với Redis** */
  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * **Lưu tin nhắn vào Redis (`put`)**
   *
   * <p>Phương thức này kiểm tra xem tin nhắn đã tồn tại trong Redis chưa.
   * Nếu chưa có, lưu vào Redis với TTL là 12 giờ.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * HistoryMessage message = new HistoryMessage("msg-123", "queue-orders", "Kafka");
   * boolean isSaved = historyMessageRepository.put(message);
   * System.out.println(isSaved); // true nếu tin nhắn được lưu thành công
   * }
   * </pre>
   *
   * @param historyMessage Tin nhắn cần lưu trữ.
   * @return `true` nếu tin nhắn được lưu thành công, `false` nếu đã tồn tại.
   */
  public Boolean put(HistoryMessage historyMessage) {
    return redisTemplate
        .opsForValue()
        .setIfAbsent(composeHeader(historyMessage), historyMessage, Duration.ofHours(12));
  }

  /**
   * **Tạo key duy nhất cho tin nhắn (`composeHeader`)**
   *
   * <p>Phương thức này tạo key để lưu vào Redis, đảm bảo mỗi tin nhắn
   * có một key duy nhất dựa trên broker, ứng dụng, điểm đến và ID.</p>
   * <p>
   * **📌 Cách tạo key:** ```
   * LOYALTY_HISTORY_MESSAGE:{BROKER}:{APPLICATION}:{DESTINATION}:{MESSAGE_ID} ```
   * <p>
   * **📌 Ví dụ tạo key:**
   * <pre>
   * {@code
   * String key = composeHeader(new HistoryMessage("msg-001", "queue-orders", "Kafka"));
   * System.out.println(key);
   * // Output: LOYALTY_HISTORY_MESSAGE:KAFKA:BASE_PROJECT:QUEUE_ORDERS:MSG_001
   * }
   * </pre>
   *
   * @param historyMessage Tin nhắn cần tạo key.
   * @return Chuỗi key duy nhất để lưu vào Redis.
   */
  private String composeHeader(HistoryMessage historyMessage) {
    return String.format(
        "LOYALTY_HISTORY_MESSAGE:%s:%s:%s:%s",
        historyMessage.getBrokerType().toUpperCase(),
        BusinessApplication.getApplicationName().toUpperCase(),
        historyMessage.getDestination().toUpperCase(),
        historyMessage.getMessageId());
  }
}