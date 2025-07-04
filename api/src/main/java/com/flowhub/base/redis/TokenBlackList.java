package com.flowhub.base.redis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * **Lớp `TokenBlackList` - Quản lý danh sách token bị vô hiệu hóa**
 *
 * <p>Lớp này lưu trữ các token đã bị vô hiệu hóa (blacklist) để ngăn chặn
 * việc sử dụng lại các token hết hạn hoặc bị thu hồi.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Khi một token bị vô hiệu hóa, thông tin về `sessionId` và thời gian hết hạn
 *   sẽ được lưu trữ trong Redis hoặc cơ chế lưu trữ tạm thời khác.
 * - `expirationSeconds` giúp xác định khoảng thời gian còn lại trước khi token
 *   bị xóa khỏi danh sách blacklist.
 * </pre>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
 * TokenBlackList blacklistedToken = new TokenBlackList("session-12345", expiryTime);
 * System.out.println(blacklistedToken.getExpirationSeconds()); // Số giây còn lại
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
public class TokenBlackList implements Serializable {

  /** ID của phiên làm việc (session) bị vô hiệu hóa** */
  private String sessionId;

  /** Thời gian hết hạn của token tính theo giây** */
  private Long expirationSeconds;

  /** Thời gian cụ thể khi token hết hạn** */
  private LocalDateTime expiration;

  /**
   * **Constructor khởi tạo đối tượng `TokenBlackList`**
   *
   * <p>Phương thức này khởi tạo một bản ghi token bị vô hiệu hóa với `sessionId`
   * và thời gian hết hạn (`expiration`). `expirationSeconds` được tính toán dựa trên khoảng thời
   * gian giữa thời điểm hiện tại và `expiration`.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
   * TokenBlackList blacklistedToken = new TokenBlackList("session-001", expiry);
   * System.out.println(blacklistedToken.getExpiration()); // Thời gian hết hạn
   * }
   * </pre>
   *
   * @param sessionId  ID của phiên làm việc bị vô hiệu hóa.
   * @param expiration Thời điểm token hết hạn.
   */
  public TokenBlackList(String sessionId, LocalDateTime expiration) {
    this.sessionId = sessionId;
    this.expirationSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiration);
    this.expiration = expiration;
  }
}