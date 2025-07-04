package com.flowhub.base.data;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Lớp này được sử dụng để chứa dữ liệu đầu vào cần mã hóa hoặc đã được mã hóa.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Lớp này được sử dụng khi cần truyền dữ liệu theo hai dạng:</p>
 * <ul>
 *   <li>📌 `encryptData`: Dữ liệu đã được mã hóa dưới dạng chuỗi.</li>
 *   <li>📌 `rawData`: Dữ liệu chưa mã hóa dưới dạng kiểu dữ liệu tùy chỉnh.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ sử dụng trong DTO truyền dữ liệu:**
 * <pre>
 * {@code
 * EncryptInput<UserDto> input = new EncryptInput<>();
 * input.setEncryptData("aGVsbG9Xb3JsZA=="); // Dữ liệu đã mã hóa
 * input.setRawData(new UserDto("John", "Doe")); // Dữ liệu gốc chưa mã hóa
 * }
 * </pre>
 * <p>
 * **📌 Ứng dụng thực tế:**
 * <ul>
 *   <li>📌 Truyền thông tin nhạy cảm giữa client và server.</li>
 *   <li>📌 Hỗ trợ các API yêu cầu mã hóa dữ liệu đầu vào.</li>
 *   <li>📌 Được sử dụng trong các hệ thống bảo mật, nơi dữ liệu cần được bảo vệ trước khi truyền tải.</li>
 * </ul>
 *
 * @param <T> Kiểu dữ liệu gốc chưa mã hóa.
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public class EncryptInput<T> {

  /**
   * <p>Trường này chứa dữ liệu đã được mã hóa dưới dạng chuỗi.</p>
   * <p>
   * **📌 Yêu cầu:** - Không được để trống (`@NotBlank`). - Thường là chuỗi được mã hóa bằng Base64,
   * AES, hoặc các thuật toán mã hóa khác.
   * <p>
   * **📌 Ví dụ dữ liệu hợp lệ:**
   * <pre>
   * "aGVsbG9Xb3JsZA=="
   * </pre>
   */
  @NotBlank
  private String encryptData;

  /**
   * <p>Trường này chứa dữ liệu gốc trước khi mã hóa.
   * Nó có thể là một đối tượng bất kỳ tùy thuộc vào kiểu generic `T`.</p>
   * <p>
   * **📌 Ví dụ dữ liệu hợp lệ:**
   * <pre>
   * new UserDto("John", "Doe")
   * </pre>
   */
  private T rawData;
}
