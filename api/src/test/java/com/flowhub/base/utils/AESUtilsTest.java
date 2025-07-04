package com.flowhub.base.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author haidv
 * @version 1.0
 */
class AESUtilsTest {

  private static final String PASSWORD = "TestPassword123!";
  private static final String SAMPLE_TEXT = "Hello, AES Unit Test!";

  @Nested
  class EncryptDecryptWithPasswordTests {

    @Test
    void testEncryptAndDecryptWithPassword() {
      String encryptedText = AESUtils.encrypt(SAMPLE_TEXT, PASSWORD);
      assertNotNull(encryptedText, "Mã hóa không được trả về null");

      String decryptedText = AESUtils.decrypt(encryptedText, PASSWORD);
      assertAll(
          () -> assertNotNull(decryptedText, "Giải mã không được trả về null"),
          () -> assertEquals(SAMPLE_TEXT, decryptedText, "Dữ liệu sau khi giải mã không khớp")
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"WrongPassword!", "123456", "AnotherPassword"})
    void testDecryptWithWrongPassword(String wrongPassword) {
      String encryptedText = AESUtils.encrypt(SAMPLE_TEXT, PASSWORD);
      assertThrows(Exception.class, () -> AESUtils.decrypt(encryptedText, wrongPassword),
                   "Giải mã với mật khẩu sai phải báo lỗi");
    }
  }

  @Nested
  class CorruptedDataTests {

    @Test
    void testDecryptWithCorruptedData() {
      String encryptedText = AESUtils.encrypt(SAMPLE_TEXT, PASSWORD);
      String corruptedText = encryptedText.substring(0, encryptedText.length() - 5) + "12345"; // Sửa đổi dữ liệu

      assertThrows(Exception.class, () -> AESUtils.decrypt(corruptedText, PASSWORD),
                   "Giải mã dữ liệu bị hỏng phải báo lỗi");
    }
  }
}
