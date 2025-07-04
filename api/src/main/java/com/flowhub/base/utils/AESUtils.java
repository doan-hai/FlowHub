package com.flowhub.base.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import lombok.experimental.UtilityClass;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;

/**
 * Utility class hỗ trợ mã hóa và giải mã dữ liệu bằng AES-256 với CBC mode. Sử dụng PBKDF2 để tạo
 * khóa từ mật khẩu giúp tăng cường bảo mật.
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
public class AESUtils {

  // Thuật toán AES sử dụng với chế độ CBC (Cipher Block Chaining) và Padding PKCS5
  private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";

  // Độ dài khóa AES (256-bit = 32 byte)
  private static final int KEY_SIZE = 256;

  // Kích thước IV (Initialization Vector) - 16 byte cho AES-128/192/256
  private static final int IV_SIZE = 16;

  // Số lần lặp của PBKDF2 để tạo khóa từ mật khẩu
  private static final int ITERATION_COUNT = 65536;

  // Độ dài salt (random) dùng để bảo vệ mật khẩu - 16 byte
  private static final int SALT_LENGTH = 16;

  // Đối tượng SecureRandom tái sử dụng để tạo Salt & IV ngẫu nhiên
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /**
   * Tạo khóa AES-256 từ mật khẩu và salt bằng thuật toán PBKDF2.
   *
   * @param password Chuỗi mật khẩu đầu vào.
   * @param salt     Mảng byte dùng làm salt.
   * @return SecretKey Khóa AES được tạo.
   * @throws Exception Nếu có lỗi khi tạo khóa.
   */
  public static SecretKey generateKeyFromPassword(String password, byte[] salt) {
    try {
      var spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
      var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new BaseException(CommonErrorDef.INTERNAL_SERVER_ERROR, e);
    }
  }

  /**
   * Tạo salt ngẫu nhiên có độ dài 16 byte.
   *
   * @return Mảng byte chứa giá trị salt.
   */
  public static byte[] generateSalt() {
    byte[] salt = new byte[SALT_LENGTH];
    SECURE_RANDOM.nextBytes(salt);
    return salt;
  }

  /**
   * Tạo IV (Initialization Vector) ngẫu nhiên có độ dài 16 byte.
   *
   * @return Đối tượng IvParameterSpec chứa IV ngẫu nhiên.
   */
  public static IvParameterSpec generateIv() {
    byte[] iv = new byte[IV_SIZE];
    SECURE_RANDOM.nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  /**
   * Mã hóa chuỗi dữ liệu bằng AES-256 và lưu Salt, IV cùng với dữ liệu mã hóa.
   *
   * @param data     Chuỗi dữ liệu cần mã hóa.
   * @param password Mật khẩu dùng để tạo khóa AES.
   * @return Chuỗi Base64 chứa Salt + IV + dữ liệu mã hóa.
   */
  public static String encrypt(String data, String password) {
    byte[] salt = generateSalt();
    IvParameterSpec iv = generateIv();
    SecretKey key = generateKeyFromPassword(password, salt);

    byte[] encryptedBytes = encryptToBytes(data, key, iv);

    // Gộp dữ liệu bằng ByteBuffer: [Salt(16)] + [IV(16)] + [CipherText]
    ByteBuffer buffer = ByteBuffer.allocate(SALT_LENGTH + IV_SIZE + encryptedBytes.length);
    buffer.put(salt);
    buffer.put(iv.getIV());
    buffer.put(encryptedBytes);

    // Chuyển đổi mảng byte sang Base64 để dễ lưu trữ và truyền tải
    return Base64.getEncoder().encodeToString(buffer.array());
  }

  /**
   * Mã hóa chuỗi dữ liệu và trả về dạng byte[].
   *
   * @param data Chuỗi dữ liệu cần mã hóa.
   * @param key  Khóa AES để mã hóa.
   * @param iv   IV sử dụng trong thuật toán AES.
   * @return Mảng byte chứa dữ liệu đã mã hóa.
   * @throws Exception Nếu có lỗi khi mã hóa.
   */
  private static byte[] encryptToBytes(String data, SecretKey key, IvParameterSpec iv) {
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance(AES_CIPHER);
      cipher.init(Cipher.ENCRYPT_MODE, key, iv);
      return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)); // Xác định encoding
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
             InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
      throw new BaseException(CommonErrorDef.NOT_ENCRYPT_SECURE_RESPONSE, e);
    }
  }

  /**
   * Giải mã chuỗi dữ liệu AES-256 từ Base64.
   *
   * @param encryptedData Chuỗi Base64 chứa Salt + IV + CipherText.
   * @param password      Mật khẩu dùng để tạo lại khóa AES.
   * @return Chuỗi dữ liệu sau khi giải mã.
   */
  public static String decrypt(String encryptedData, String password) {
    // Giải mã dữ liệu từ Base64 thành mảng byte
    byte[] combined = Base64.getDecoder().decode(encryptedData);

    // Tách dữ liệu từ mảng byte: [Salt(16)] + [IV(16)] + [CipherText]
    ByteBuffer buffer = ByteBuffer.wrap(combined);
    byte[] salt = new byte[SALT_LENGTH];
    byte[] ivBytes = new byte[IV_SIZE];
    byte[] cipherText = new byte[buffer.remaining() - SALT_LENGTH - IV_SIZE];

    buffer.get(salt);
    buffer.get(ivBytes);
    buffer.get(cipherText);

    // Tạo lại khóa từ mật khẩu và Salt
    SecretKey key = generateKeyFromPassword(password, salt);
    return decryptFromBytes(cipherText, key, new IvParameterSpec(ivBytes));
  }

  /**
   * Giải mã dữ liệu từ mảng byte.
   *
   * @param encryptedBytes Mảng byte chứa dữ liệu đã mã hóa.
   * @param key            Khóa AES để giải mã.
   * @param iv             IV sử dụng trong thuật toán AES.
   * @return Chuỗi dữ liệu sau khi giải mã.
   * @throws Exception Nếu có lỗi khi giải mã.
   */
  private static String decryptFromBytes(byte[] encryptedBytes, SecretKey key, IvParameterSpec iv) {
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance(AES_CIPHER);
      cipher.init(Cipher.DECRYPT_MODE, key, iv);
      byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

      // Dọn dẹp bộ nhớ tránh lộ dữ liệu
      Arrays.fill(encryptedBytes, (byte) 0);

      return new String(decryptedBytes, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
             InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
      throw new BaseException(CommonErrorDef.NOT_DECRYPT_SECURE_REQUEST, e);
    }
  }
}
