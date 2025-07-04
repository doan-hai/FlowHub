package com.flowhub.base.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * **Lớp `SecureInput` - Xác thực dữ liệu an toàn bằng HMAC**
 *
 * <p>Lớp này cung cấp cơ chế bảo mật để kiểm tra tính toàn vẹn của dữ liệu
 * bằng cách sử dụng **HMAC-SHA256** để tạo chữ ký số (`secure`).</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi lớp kế thừa `SecureInput` cần triển khai phương thức `buildDataCheckSecure()`
 * để cung cấp dữ liệu đầu vào cho quá trình tạo chữ ký số.</p>
 * <p>
 * **📌 Ví dụ lớp con kế thừa `SecureInput`:**
 * <pre>
 * {@code
 * public class UserSecureInput extends SecureInput {
 *     private String username;
 *     private String email;
 *
 *     @Override
 *     protected String buildDataCheckSecure() {
 *         return username + ":" + email;
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Cách kiểm tra tính hợp lệ của dữ liệu:**
 * <pre>
 * {@code
 * UserSecureInput input = new UserSecureInput();
 * input.setUsername("john_doe");
 * input.setEmail("john@example.com");
 * String secureKey = "my-secret-key";
 * input.setSecure(input.buildSecure(secureKey)); // Tạo chữ ký số
 *
 * boolean isValid = input.isValidData(secureKey); // Kiểm tra chữ ký số
 * System.out.println("Dữ liệu hợp lệ: " + isValid);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Setter
@Getter
public abstract class SecureInput {

  @Schema(description = "Chuỗi bảo mật (chữ ký số HMAC-SHA256)")
  private String secure;

  /**
   * **Kiểm tra tính hợp lệ của dữ liệu (`isValidData`)**
   *
   * <p>Phương thức này kiểm tra xem chữ ký số (`secure`) có khớp với dữ liệu hiện tại không.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Nếu `secure` hoặc `secureKey` rỗng, mặc định dữ liệu hợp lệ (`true`).</li>
   *   <li>📌 Nếu `secure` khớp với chữ ký số được tạo từ `buildSecure()`, dữ liệu hợp lệ.</li>
   *   <li>📌 Ngược lại, dữ liệu không hợp lệ (`false`).</li>
   * </ul>
   * <p>
   * **📌 Ví dụ kiểm tra:**
   * <pre>
   * {@code
   * boolean isValid = input.isValidData("my-secret-key");
   * System.out.println("Dữ liệu hợp lệ: " + isValid);
   * }
   * </pre>
   *
   * @param secureKey Khóa bảo mật dùng để kiểm tra.
   * @return `true` nếu dữ liệu hợp lệ, `false` nếu không hợp lệ.
   */
  public boolean isValidData(String secureKey) {
    return StringUtils.isBlank(secure)
        || StringUtils.isBlank(secureKey)
        || secure.equals(buildSecure(secureKey));
  }

  /**
   * **Tạo chữ ký số (`buildSecure`)**
   *
   * <p>Phương thức này tạo một chữ ký số (`secure`) sử dụng thuật toán HMAC-SHA256.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Nếu `secureKey` rỗng, trả về `null`.</li>
   *   <li>📌 Sử dụng `HmacUtils(HmacAlgorithms. HMAC_SHA_256, secureKey).hmacHex` để tạo chữ ký số từ dữ liệu `buildDataCheckSecure()`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ tạo chữ ký số:**
   * <pre>
   * {@code
   * String signature = input.buildSecure("my-secret-key");
   * System.out.println("Chữ ký số: " + signature);
   * }
   * </pre>
   *
   * @param secureKey Khóa bảo mật dùng để tạo chữ ký số.
   * @return Chữ ký số dạng Base64 hoặc `null` nếu không có khóa.
   */
  public String buildSecure(String secureKey) {
    return StringUtils.isBlank(secureKey)
        ? null
        : new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secureKey).hmacHex(buildDataCheckSecure());
  }

  /**
   * **Phương thức trừu tượng để cung cấp dữ liệu cần mã hóa**
   *
   * <p>Mỗi lớp con cần triển khai phương thức này để xác định
   * dữ liệu nào sẽ được sử dụng trong quá trình tạo chữ ký số.</p>
   * <p>
   * **📌 Ví dụ trong lớp `UserSecureInput`:**
   * <pre>
   * {@code
   * @Override
   * protected String buildDataCheckSecure() {
   *     return username + ":" + email;
   * }
   * }
   * </pre>
   *
   * @return Chuỗi dữ liệu cần bảo vệ.
   */
  protected abstract String buildDataCheckSecure();
}
