package com.flowhub.base.redis;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * **Lớp `TokenBlackListRepository` - Quản lý danh sách token bị vô hiệu hóa trong Redis**
 *
 * <p>Lớp này cung cấp các phương thức để thao tác với danh sách token bị vô hiệu hóa
 * trong Redis. Token bị vô hiệu hóa sẽ được lưu với một thời gian hết hạn để đảm bảo không thể sử
 * dụng lại sau khi bị thu hồi.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Khi một token bị thu hồi, nó sẽ được lưu vào Redis với `sessionId` làm key.
 * - Redis tự động xóa token sau khoảng thời gian hết hạn (`expirationSeconds`).
 * - Khi cần kiểm tra một token có bị vô hiệu hóa không, ta gọi `find()` để lấy dữ liệu từ Redis.
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class TokenBlackListRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * **Tìm token bị vô hiệu hóa trong Redis (`find`)**
   *
   * <p>Phương thức này tìm kiếm một `TokenBlackList` trong Redis dựa trên `sessionId`
   * đã được lưu trữ. Nếu không tìm thấy, trả về `Optional.empty()`.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Optional<TokenBlackList> blacklistedToken = tokenBlackListRepository.find("session-001");
   * if (blacklistedToken.isPresent()) {
   *     System.out.println("Token đã bị vô hiệu hóa");
   * }
   * }
   * </pre>
   *
   * @param key `sessionId` của token.
   * @return `Optional<TokenBlackList>` nếu tồn tại, `Optional.empty()` nếu không tìm thấy.
   */
  public Optional<TokenBlackList> find(String key) {
    return Optional.ofNullable(
        (TokenBlackList) this.redisTemplate.opsForValue().get(composeHeader(key)));
  }

  /**
   * **Lưu token vào danh sách bị vô hiệu hóa trong Redis (`put`)**
   *
   * <p>Phương thức này lưu một `TokenBlackList` vào Redis và đặt thời gian hết hạn
   * để token tự động bị xóa khỏi danh sách blacklist sau khi hết hạn.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30);
   * TokenBlackList blacklistedToken = new TokenBlackList("session-001", expiryTime);
   * tokenBlackListRepository.put(blacklistedToken);
   * }
   * </pre>
   *
   * @param tokenBlackList Đối tượng `TokenBlackList` cần lưu trữ.
   */
  public void put(TokenBlackList tokenBlackList) {
    this.redisTemplate
        .opsForValue()
        .set(
            composeHeader(tokenBlackList.getSessionId()),
            tokenBlackList,
            Duration.ofSeconds(tokenBlackList.getExpirationSeconds()));
  }

  /**
   * **Tạo key chuẩn hóa để lưu trữ token trong Redis (`composeHeader`)**
   *
   * <p>Phương thức này tạo key duy nhất cho mỗi token bị vô hiệu hóa bằng cách
   * thêm tiền tố `"TokenBlackList:"` vào `sessionId`, giúp tránh xung đột key trong Redis.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String key = composeHeader("session-001");
   * System.out.println(key); // "TokenBlackList:session-001"
   * }
   * </pre>
   *
   * @param key `sessionId` của token.
   * @return Chuỗi key duy nhất để lưu trong Redis.
   */
  private String composeHeader(String key) {
    return String.format("TokenBlackList:%s", key);
  }
}