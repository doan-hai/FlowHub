package com.flowhub.base.annotations.deserializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import org.apache.commons.lang3.StringUtils;
import com.flowhub.base.annotations.deserializer.DateJsonDeserialize.Type;
import com.flowhub.base.constant.DateConstant;
import com.flowhub.base.exception.DateTimeFormatException;
import com.flowhub.base.utils.DateUtils;
import com.flowhub.base.utils.MessageUtils;

/**
 * **Deserializer cho kiểu dữ liệu `Temporal`**
 *
 * <p>Class này giúp chuyển đổi một chuỗi JSON thành các kiểu dữ liệu `LocalDate`, `LocalTime`,
 * `LocalDateTime` dựa trên format được cung cấp từ annotation `@JsonFormat` hoặc
 * `@DateJsonDeserialize`.</p>
 *
 * @author haidv
 * @version 1.0
 */
public class TemporalDeserializer extends JsonDeserializer<Temporal> {

  @Override
  public Temporal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    var dateStr = p.getText();
    var fieldName = p.currentName(); // Lấy tên field bị lỗi

    // Lấy class chứa field
    Class<?> clazz = (p.currentValue() != null) ? p.currentValue().getClass() : null;
    if (clazz == null) {
      throw new DateTimeFormatException("Unable to determine the target class", fieldName, dateStr);
    }

    // Lấy thông tin của field qua reflection
    Field field;
    try {
      field = clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      throw new DateTimeFormatException("Field not found in class", fieldName, dateStr);
    }

    // Lấy định dạng từ annotation @JsonFormat nếu có
    DateTimeFormatter formatter = null;
    String pattern = null;
    String message = null;
    Type type = Type.DATE; // Mặc định là LocalDate

    // Kiểm tra các annotation để xác định loại dữ liệu
    if (field.isAnnotationPresent(JsonFormat.class)) {
      var jsonFormat = field.getAnnotation(JsonFormat.class);
      if (!jsonFormat.pattern().isEmpty()) {
        pattern = jsonFormat.pattern();
        formatter = DateUtils.getConfigFormatter().get(jsonFormat.pattern());
      }
    }

    if (field.isAnnotationPresent(DateJsonDeserialize.class)) {
      var dateJsonDeserialize = field.getAnnotation(DateJsonDeserialize.class);
      type = dateJsonDeserialize.type();
      message = dateJsonDeserialize.message();
    }

    // Gán formatter mặc định nếu chưa có
    formatter = (formatter != null) ? formatter :
        switch (type) {
          case Type.DATE,
               Type.START_OF_DAY,
               Type.END_OF_DAY -> DateUtils.DD_MM_YYYY_DASH_FORMATTER;
          case Type.DATETIME -> DateUtils.DD_MM_YYYY_HH_MM_SS_DASH_FORMATTER;
          case Type.TIME -> DateUtils.HH_MM_SS_FORMATTER;
        };

    // Gán pattern mặc định nếu chưa có
    pattern = (StringUtils.isBlank(pattern)) ?
        switch (type) {
          case Type.DATE,
               Type.START_OF_DAY,
               Type.END_OF_DAY -> DateConstant.DD_MM_YYYY_DASH;
          case Type.DATETIME -> DateConstant.DD_MM_YYYY_HH_MM_SS_DASH;
          case Type.TIME -> DateConstant.HH_MM_SS;
        } : pattern;

    // Gán message mặc định nếu chưa có
    message = (StringUtils.isBlank(message)) ?
        String.format("Invalid date format. Expected format: %s", pattern) : message;

    try {
      return switch (type) {
        case Type.DATE -> LocalDate.parse(dateStr, formatter);
        case Type.DATETIME -> LocalDateTime.parse(dateStr, formatter);
        case Type.TIME -> LocalTime.parse(dateStr, formatter);
        case Type.START_OF_DAY -> LocalDate.parse(dateStr, formatter).atStartOfDay();
        case Type.END_OF_DAY -> LocalDate.parse(dateStr, formatter).atTime(23, 59, 59, 999_999_999);
      };
    } catch (DateTimeParseException e) {
      var errorMessage = MessageUtils.resolveMessage(message, pattern);
      throw new DateTimeFormatException(errorMessage, fieldName, dateStr);
    }
  }
}
