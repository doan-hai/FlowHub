package com.flowhub.base.utils;

import java.util.ResourceBundle;
import lombok.experimental.UtilityClass;

/**
 * @author haidv
 * @version 1.0
 */
@UtilityClass
public class MessageUtils {

  /**
   * **Xử lý message: Có thể là template i18n hoặc message tùy chỉnh**
   *
   * @param messageCode Chuỗi message từ annotation hoặc i18n key.
   * @param params      Các tham số động `{0}`, `{1}`, `{2}`.
   * @return Chuỗi sau khi thay thế biến `{}`.
   */
  public static String resolveMessage(String messageCode, Object... params) {
    String message = messageCode;

    // Nếu message là template i18n (có dạng "{validation.xxx}")
    if (messageCode.startsWith("{") && messageCode.endsWith("}")) {
      String i18nKey = messageCode.substring(1,
                                             messageCode.length() - 1); // Loại bỏ `{}` để lấy key
      message = getLocalizedMessage(i18nKey);
    }

    // Thay thế các biến `{0}`, `{1}`, `{2}` nếu có tham số
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        message = message.replace("{" + i + "}", params[i].toString());
      }
    }

    return message;
  }

  /**
   * **Lấy thông báo lỗi từ `messages.properties`**
   *
   * @param key Key của message trong `messages.properties`.
   * @return Chuỗi message từ i18n hoặc key nếu không tìm thấy.
   */
  public static String getLocalizedMessage(String key) {
    ResourceBundle bundle = ResourceBundle.getBundle("lang/messages", RequestUtils.extractLocale());
    return bundle.containsKey(key) ? bundle.getString(key) : key; // Trả về key nếu không tìm thấy
  }
}
