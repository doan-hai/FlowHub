package com.flowhub.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * **Lớp `JsonUtils` - Hỗ trợ chuyển đổi JSON và Object**
 *
 * <p>Class tiện ích này cung cấp các phương thức để chuyển đổi giữa `Object` và `JSON`
 * sử dụng thư viện Jackson.</p>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * String jsonString = JsonUtils.toJson(new User("John", 30));
 * User user = JsonUtils.fromJson(jsonString, User.class);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
@Slf4j
public class JsonUtils {

  /**
   * **Chuyển đổi Object thành chuỗi JSON (`toJson`)**
   *
   * <p>Phương thức này nhận một object và chuyển đổi nó thành chuỗi JSON.</p>
   * <p>
   * **📌 Cấu hình mặc định:**
   * <pre>
   * - Không ghi các thuộc tính `null` (`Include.NON_NULL`).
   * - Không ghi ngày giờ dưới dạng timestamp.
   * - Bỏ qua lỗi nếu object không có field nào để serialize (`FAIL_ON_EMPTY_BEANS` = false).
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String jsonString = JsonUtils.toJson(new User("Alice", 25));
   * System.out.println(jsonString); // {"name": "Alice", "age": 25}
   * }
   * </pre>
   *
   * @param object Đối tượng cần chuyển đổi sang JSON.
   * @return Chuỗi JSON hoặc chuỗi rỗng nếu có lỗi.
   */
  public static String toJson(Object object) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new Jdk8Module());
    String jsonString = StringUtils.EMPTY;
    try {
      jsonString = mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("Can't build json from object", e);
    }
    return jsonString;
  }

  /**
   * **Chuyển đổi JSON thành Object (`fromJson`)**
   *
   * <p>Phương thức này nhận một chuỗi JSON và chuyển đổi nó thành một Object của class chỉ
   * định.</p>
   * <p>
   * **📌 Cấu hình mặc định:**
   * <pre>
   * - Bỏ qua các thuộc tính không xác định trong JSON (`FAIL_ON_UNKNOWN_PROPERTIES` = false).
   * - Hỗ trợ xử lý `JavaTimeModule` để làm việc với `LocalDateTime`.
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String json = "{\"name\": \"Bob\", \"age\": 40}";
   * User user = JsonUtils.fromJson(json, User.class);
   * }
   * </pre>
   *
   * @param json      Chuỗi JSON cần chuyển đổi.
   * @param valueType Class của đối tượng cần chuyển đổi.
   * @param <T>       Kiểu dữ liệu của đối tượng kết quả.
   * @return Đối tượng tương ứng hoặc `null` nếu có lỗi.
   */
  public static <T> T fromJson(String json, Class<T> valueType) {
    if (json == null) {
      return null;
    }

    T object = null;
    try {
      var objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.registerModule(new JavaTimeModule());
      object = objectMapper.readValue(json, valueType);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return object;
  }

  /**
   * **Chuyển đổi Object thành một Object khác (`fromJson`)**
   *
   * <p>Phương thức này cho phép chuyển đổi một Object thành một Object của class chỉ định,
   * hữu ích khi làm việc với các đối tượng trung gian như `Map` hoặc JSON đã parse.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Map<String, Object> data = Map.of("name", "Charlie", "age", 35);
   * User user = JsonUtils.fromJson(data, User.class);
   * }
   * </pre>
   *
   * @param json      Đối tượng cần chuyển đổi (có thể là `Map` hoặc Object đã parse).
   * @param valueType Class của đối tượng cần chuyển đổi.
   * @param <T>       Kiểu dữ liệu của đối tượng kết quả.
   * @return Đối tượng tương ứng hoặc `null` nếu có lỗi.
   */
  public static <T> T fromJson(Object json, Class<T> valueType) {
    if (json == null) {
      return null;
    }

    T object = null;
    try {
      var objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.registerModule(new JavaTimeModule());
      object = objectMapper.convertValue(json, valueType);
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return object;
  }

  /**
   * **Chuyển đổi JSON thành Object với kiểu dữ liệu tham số (`fromJson`)**
   *
   * <p>Phương thức này cho phép chuyển đổi JSON thành Object có kiểu dữ liệu tham số,
   * giúp làm việc với các kiểu dữ liệu generic như `List<T>` hoặc `Map<K, V>`.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String json = "[{\"name\": \"David\", \"age\": 28}, {\"name\": \"Emma\", \"age\": 22}]";
   * List<User> users = JsonUtils.fromJson(json, List.class, User.class);
   * }
   * </pre>
   *
   * @param json             Chuỗi JSON cần chuyển đổi.
   * @param parentType       Class cha (ví dụ: `List.class` hoặc `Map.class`).
   * @param parameterClasses Các class con của kiểu dữ liệu tham số (ví dụ: `User.class`).
   * @param <T>              Kiểu dữ liệu của đối tượng kết quả.
   * @return Đối tượng tương ứng hoặc `null` nếu có lỗi.
   */
  public static <T> T fromJson(String json, Class<?> parentType, Class<?>... parameterClasses) {
    if (json == null) {
      return null;
    }

    T object = null;
    try {
      var objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.registerModule(new JavaTimeModule());
      JavaType type =
          objectMapper.getTypeFactory().constructParametricType(parentType, parameterClasses);
      object = objectMapper.readValue(json, type);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    return object;
  }
}