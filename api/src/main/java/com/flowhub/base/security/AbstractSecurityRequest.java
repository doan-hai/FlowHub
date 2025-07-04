package com.flowhub.base.security;

/**
 * **Lớp `AbstractSecurityRequest` - Lớp trừu tượng cho xử lý bảo mật dữ liệu**
 *
 * <p>Lớp này cung cấp các phương thức trừu tượng để xử lý bảo mật dữ liệu,
 * bao gồm mã hóa, giải mã và lấy khóa kiểm tra tính toàn vẹn.</p>
 * <pre>
 * **📌 Cách hoạt động:**
 * - Các lớp con kế thừa `AbstractSecurityRequest` phải triển khai các phương thức
 *   để thực hiện mã hóa và giải mã dữ liệu tùy theo thuật toán bảo mật được sử dụng.
 * - `getChecksumKey()` được sử dụng để lấy khóa kiểm tra tính toàn vẹn của dữ liệu.
 * </pre>
 * **📌 Ví dụ triển khai lớp con:**
 * <pre>
 * {@code
 * public class AESecurityRequest extends AbstractSecurityRequest {
 *     @Override
 *     public String encryptData(String decryptStr) {
 *         return AESUtil.encrypt(decryptStr, SECRET_KEY);
 *     }
 *
 *     @Override
 *     public String decryptData(String encryptStr) {
 *         return AESUtil.decrypt(encryptStr, SECRET_KEY);
 *     }
 *
 *     @Override
 *     public String getChecksumKey() {
 *         return SECRET_KEY;
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public abstract class AbstractSecurityRequest {

  /**
   * **Mã hóa dữ liệu (`encryptData`)**
   *
   * <p>Phương thức này nhận một chuỗi đã giải mã và trả về chuỗi đã được mã hóa.
   * Các lớp con phải cung cấp thuật toán mã hóa cụ thể.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String encrypted = securityRequest.encryptData("my_secret_data");
   * }
   * </pre>
   *
   * @param decryptStr Dữ liệu đầu vào cần mã hóa.
   * @return Chuỗi đã được mã hóa.
   */
  public abstract String encryptData(String decryptStr);

  /**
   * **Giải mã dữ liệu (`decryptData`)**
   *
   * <p>Phương thức này nhận một chuỗi đã mã hóa và trả về chuỗi đã được giải mã.
   * Các lớp con phải cung cấp thuật toán giải mã cụ thể.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String decrypted = securityRequest.decryptData("encrypted_string");
   * }
   * </pre>
   *
   * @param encryptStr Dữ liệu đầu vào đã mã hóa.
   * @return Chuỗi đã được giải mã.
   */
  public abstract String decryptData(String encryptStr);

  /**
   * **Lấy khóa kiểm tra (`getChecksumKey`)**
   *
   * <p>Phương thức này trả về khóa bảo mật được sử dụng để kiểm tra tính toàn vẹn dữ liệu.
   * Các lớp con phải triển khai cơ chế lấy khóa phù hợp với thuật toán bảo mật của chúng.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String checksumKey = securityRequest.getChecksumKey();
   * }
   * </pre>
   *
   * @return Khóa bảo mật dùng để kiểm tra dữ liệu.
   */
  public abstract String getChecksumKey();
}