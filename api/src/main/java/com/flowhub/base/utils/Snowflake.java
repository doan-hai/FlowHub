package com.flowhub.base.utils;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import lombok.Getter;
import lombok.Setter;

/**
 * **Snowflake Distributed ID Generator**
 *
 * <p>Snowflake là một thuật toán tạo ID phân tán được lấy cảm hứng từ Twitter Snowflake.
 * Nó tạo ra các ID duy nhất theo thời gian với cấu trúc như sau:</p>
 *
 * <pre>
 *  +------+----------------------+----------------+-----------+
 *  | Sign |     Timestamp        |   Node ID     | Sequence  |
 *  +------+----------------------+----------------+-----------+
 *  |  1b  |        41b           |      10b      |   12b     |
 *  +------+----------------------+----------------+-----------+
 * </pre>
 * <p><pre>
 * **Cấu trúc ID (64-bit):**
 * - **1 bit dấu (unused)**: Luôn bằng `0`.
 * - **41 bit timestamp**: Thời gian tính từ epoch tùy chỉnh (`customEpoch`).
 * - **10 bit node ID**: Định danh máy chủ (hoặc container) trong hệ thống phân tán.
 * - **12 bit sequence**: Bộ đếm để đảm bảo không có xung đột trong cùng một millisecond.</pre>
 *
 * <p>📌 **Lưu ý:** Lớp này được thiết kế để dùng như một Singleton.</p>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class Snowflake {

  /** Bit không sử dụng (1 bit dấu luôn là 0). */
  private static final int UNUSED_BITS = 1;

  /** Số bit dành cho timestamp (41 bits). */
  private static final int EPOCH_BITS = 41;

  /** Số bit dành cho node ID (10 bits). */
  private static final int NODE_ID_BITS = 10;

  /** Số bit dành cho sequence (12 bits). */
  private static final int SEQUENCE_BITS = 12;

  /** Giới hạn tối đa của node ID (2^10 - 1 = 1023). */
  private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;

  /** Giới hạn tối đa của sequence (2^12 - 1 = 4095). */
  private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

  /** Mốc thời gian tùy chỉnh (Epoch mặc định: 01-01-2015 00:00:00 UTC). */
  private static final long DEFAULT_CUSTOM_EPOCH = 1420070400000L;

  private static Snowflake instance;

  /** Node ID đại diện cho máy chủ hiện tại. */
  private long nodeId;

  /** Thời gian bắt đầu của Epoch tùy chỉnh. */
  private long customEpoch;

  /** Lưu timestamp của ID cuối cùng được tạo. */
  private volatile long lastTimestamp = -1L;

  /** Bộ đếm sequence trong cùng một millisecond. */
  private volatile long sequence = 0L;

  private Snowflake() {
  }


  /**
   * **Lấy Snowflake instance (Singleton), tự động tạo node ID.**
   *
   * @return Instance của Snowflake.
   */
  public static Snowflake getInstance() {
    return getInstance(createNodeId());
  }


  /**
   * **Lấy Snowflake instance với node ID do người dùng cung cấp.**
   *
   * @param nodeId Node ID của máy chủ.
   * @return Instance của Snowflake.
   */
  public static Snowflake getInstance(long nodeId) {
    return getInstance(nodeId, DEFAULT_CUSTOM_EPOCH);
  }

  /**
   * **Lấy Snowflake instance với node ID và epoch tùy chỉnh.**
   *
   * @param nodeId      Node ID của máy chủ.
   * @param customEpoch Epoch tùy chỉnh.
   * @return Instance của Snowflake.
   */
  public static Snowflake getInstance(long nodeId, long customEpoch) {
    if (instance == null) {
      Snowflake snowflake = new Snowflake();
      snowflake.setCustomEpoch(customEpoch);
      snowflake.setNodeId(nodeId);
      instance = snowflake;
    }
    return instance;
  }

  /**
   * **Tạo node ID dựa trên địa chỉ MAC.**
   *
   * <pre>
   * - Nếu có thể lấy địa chỉ MAC, sẽ sử dụng hash của nó.
   * - Nếu không thể lấy địa chỉ MAC, sẽ tạo số ngẫu nhiên. </pre>
   *
   * @return Node ID (0 → 1023).
   */
  private static long createNodeId() {
    long nodeId;
    try {
      StringBuilder sb = new StringBuilder();
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface networkInterface = networkInterfaces.nextElement();
        byte[] mac = networkInterface.getHardwareAddress();
        if (mac != null) {
          for (byte macPort : mac) {
            sb.append(String.format("%02X", macPort));
          }
        }
      }
      nodeId = sb.toString().hashCode();
    } catch (Exception ex) {
      nodeId = (new SecureRandom().nextInt());
    }
    nodeId = nodeId & MAX_NODE_ID;
    return nodeId;
  }

  /**
   * **Tạo ID tiếp theo theo thuật toán Snowflake.**
   *
   * <p>Cách hoạt động:</p>
   * <pre>
   * - Lấy timestamp hiện tại (đã trừ `customEpoch`).
   * - Nếu timestamp nhỏ hơn `lastTimestamp`, hệ thống có lỗi đồng hồ.
   * - Nếu timestamp bằng `lastTimestamp` (trong cùng 1 millisecond):
   *      - Tăng `sequence`.
   *      - Nếu `sequence` vượt quá giới hạn, đợi đến millisecond tiếp theo.
   * - Nếu timestamp khác `lastTimestamp`, reset `sequence = 0`.
   * - Cập nhật `lastTimestamp` và tạo ID với cấu trúc:
   *
   *   ID = (timestamp << (NODE_ID_BITS + SEQUENCE_BITS))
   *        | (nodeId << SEQUENCE_BITS)
   *        | sequence;
   *
   * <p><b>Lưu ý:</b></p>
   * - **Đồng bộ hóa (`synchronized`)** để tránh xung đột giữa các luồng.
   * - **Nếu hệ thống lùi thời gian (`clock rollback`), ném lỗi `IllegalStateException`**.
   * - **Nếu `sequence` đạt giới hạn trong 1ms, phải đợi sang millisecond tiếp theo**.
   * </pre>
   *
   * @return ID duy nhất dạng `long`.
   * @throws IllegalStateException nếu đồng hồ hệ thống lùi về quá khứ.
   */
  public synchronized long nextId() {
    // Lấy timestamp hiện tại (tính từ customEpoch)
    long currentTimestamp = timestamp();

    // Kiểm tra nếu đồng hồ hệ thống bị lùi thời gian
    if (currentTimestamp < lastTimestamp) {
      throw new IllegalStateException("Invalid System Clock!");
    }

    // Nếu timestamp giống với lần trước (cùng 1ms)
    if (currentTimestamp == lastTimestamp) {
      sequence = (sequence + 1) & MAX_SEQUENCE; // Tăng sequence
      if (sequence == 0) {
        // Nếu sequence đạt giới hạn, đợi đến millisecond tiếp theo
        currentTimestamp = waitNextMillis(currentTimestamp);
      }
    } else {
      // Nếu timestamp thay đổi, reset sequence
      sequence = 0;
    }

    // Cập nhật lastTimestamp để tránh trùng lặp
    lastTimestamp = currentTimestamp;

    // Tạo ID bằng cách dịch bit timestamp, nodeId và sequence
    return currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
        | (nodeId << SEQUENCE_BITS)
        | sequence;
  }

  /**
   * **Lấy timestamp hiện tại (theo milliseconds), đã điều chỉnh theo custom epoch.**
   *
   * @return Timestamp hiện tại.
   */
  private long timestamp() {
    return Instant.now().toEpochMilli() - customEpoch;
  }

  /**
   * **Chờ đến millisecond tiếp theo nếu sequence bị tràn.**
   *
   * @param currentTimestamp Timestamp hiện tại.
   * @return Timestamp mới.
   */
  private long waitNextMillis(long currentTimestamp) {
    while (currentTimestamp == lastTimestamp) {
      currentTimestamp = timestamp();
    }
    return currentTimestamp;
  }

  /**
   * **Phân tích ID để lấy các thành phần timestamp, node ID, và sequence.**
   *
   * @param id ID cần phân tích.
   * @return Mảng `{timestamp, nodeId, sequence}`.
   */
  public long[] parse(long id) {
    long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
    long maskSequence = (1L << SEQUENCE_BITS) - 1;

    long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS)) + customEpoch;
    long extractNodeId = (id & maskNodeId) >> SEQUENCE_BITS;
    long extractSequence = id & maskSequence;

    return new long[]{timestamp, extractNodeId, extractSequence};
  }

  @Override
  public String toString() {
    return "Snowflake Settings [EPOCH_BITS="
        + EPOCH_BITS
        + ", NODE_ID_BITS="
        + NODE_ID_BITS
        + ", SEQUENCE_BITS="
        + SEQUENCE_BITS
        + ", CUSTOM_EPOCH="
        + customEpoch
        + ", NodeId="
        + nodeId
        + "]";
  }
}
