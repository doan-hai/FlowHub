package com.flowhub.base.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;
import com.flowhub.base.utils.AESUtils;

/**
 * **Lớp `SecurityRequest` - Xử lý mã hóa và giải mã dữ liệu**
 *
 * <p>Lớp này mở rộng `AbstractSecurityRequest` để cung cấp các phương thức mã hóa
 * và giải mã dữ liệu bằng thuật toán AES. Nó sử dụng `AESUtils` để thực hiện mã hóa và giải mã bằng
 * các khóa bí mật được cấu hình trong `application.properties`.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Các khóa bí mật (`privateKeyEncrypt` và `privateKeyChecksum`) được inject từ cấu hình.
 * - `encryptData()` sử dụng `AESUtils.encrypt()` để mã hóa dữ liệu.
 * - `decryptData()` sử dụng `AESUtils.decrypt()` để giải mã dữ liệu.
 * - `getChecksumKey()` trả về khóa dùng để kiểm tra checksum.
 * </pre>
 * <p>
 * **📌 Ví dụ cấu hình trong `application.properties`:**
 * <pre>
 * {@code
 * custom.properties.security.request.private-key-checksum=your-checksum-key
 * custom.properties.security.request.private-key-encrypt=your-encryption-key
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * SecurityRequest securityRequest = new SecurityRequest();
 * String encrypted = securityRequest.encryptData("my_secret_data");
 * String decrypted = securityRequest.decryptData(encrypted);
 * System.out.println(decrypted); // my_secret_data
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityRequest extends AbstractSecurityRequest {

  /** Khóa bí mật dùng để kiểm tra checksum** */
  @Value("${custom.properties.security.request.private-key-checksum}")
  private String privateKeyChecksum;

  /** Khóa bí mật dùng để mã hóa và giải mã dữ liệu** */
  @Value("${custom.properties.security.request.private-key-encrypt}")
  private String privateKeyEncrypt;

  /**
   * **Mã hóa dữ liệu (`encryptData`)**
   *
   * <p>Phương thức này sử dụng khóa bí mật `privateKeyEncrypt` để mã hóa chuỗi đầu vào
   * bằng thuật toán AES.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String encrypted = securityRequest.encryptData("my_secret");
   * System.out.println(encrypted);
   * }
   * </pre>
   *
   * @param decryptStr Chuỗi cần mã hóa.
   * @return Chuỗi đã được mã hóa.
   * @throws RuntimeException Nếu quá trình mã hóa thất bại.
   */
  @Override
  public String encryptData(String decryptStr) {
    try {
      return AESUtils.encrypt(privateKeyEncrypt, decryptStr);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new BaseException(CommonErrorDef.NOT_ENCRYPT_SECURE_RESPONSE);
    }
  }

  /**
   * **Giải mã dữ liệu (`decryptData`)**
   *
   * <p>Phương thức này sử dụng khóa bí mật `privateKeyEncrypt` để giải mã chuỗi đầu vào
   * đã được mã hóa bằng thuật toán AES.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String decrypted = securityRequest.decryptData(encryptedData);
   * System.out.println(decrypted);
   * }
   * </pre>
   *
   * @param encryptStr Chuỗi đã mã hóa cần giải mã.
   * @return Chuỗi sau khi giải mã.
   * @throws RuntimeException Nếu quá trình giải mã thất bại.
   */
  @Override
  public String decryptData(String encryptStr) {
    try {
      return AESUtils.decrypt(privateKeyEncrypt, encryptStr);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new BaseException(CommonErrorDef.NOT_DECRYPT_SECURE_REQUEST);
    }
  }

  /**
   * **Lấy khóa kiểm tra checksum (`getChecksumKey`)**
   *
   * <p>Phương thức này trả về khóa bí mật dùng để kiểm tra checksum của dữ liệu.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String checksumKey = securityRequest.getChecksumKey();
   * System.out.println(checksumKey);
   * }
   * </pre>
   *
   * @return Chuỗi khóa checksum.
   */
  @Override
  public String getChecksumKey() {
    return privateKeyChecksum;
  }
}