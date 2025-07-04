package com.flowhub.base.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * **Cấu hình Redis (`RedisConfiguration`)**
 *
 * <p>Lớp này chịu trách nhiệm cấu hình Redis cho ứng dụng, bao gồm thiết lập
 * `RedisTemplate` để làm việc với Redis một cách linh hoạt.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Tạo `RedisTemplate<String, Object>` để thao tác với Redis.</li>
 *   <li>✅ Cấu hình serializer cho **key** và **value** để đảm bảo dữ liệu được lưu trữ chính xác.</li>
 *   <li>✅ Cấu hình `ObjectMapper` để hỗ trợ serializing/deserializing các đối tượng Java.</li>
 *   <li>✅ Hỗ trợ **Java 8 Date/Time API** khi làm việc với Redis.</li>
 * </ul>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>`RedisTemplate` được sử dụng để lưu trữ và truy xuất dữ liệu từ Redis. Để đảm bảo dữ liệu
 * được đọc và ghi đúng cách, cần thiết lập serializer cho cả key và value:</p>
 * <ul>
 *   <li>📌 **Key**: Được cấu hình bằng `StringRedisSerializer` để lưu trữ dưới dạng chuỗi.</li>
 *   <li>📌 **Value**: Sử dụng `GenericJackson2JsonRedisSerializer` để hỗ trợ JSON, giúp dễ dàng làm việc với các đối tượng Java.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ sử dụng `RedisTemplate`:**
 * <pre>
 * {@code
 * @Autowired
 * private RedisTemplate<String, Object> redisTemplate;
 *
 * public void saveToRedis() {
 *     redisTemplate.opsForValue().set("user:123", new User("John", "Doe"));
 * }
 *
 * public User getFromRedis() {
 *     return (User) redisTemplate.opsForValue().get("user:123");
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
public class RedisConfiguration {

  /**
   * **Cấu hình `RedisTemplate`**
   *
   * <p>Phương thức này tạo một `RedisTemplate<String, Object>` với các serializer
   * được thiết lập để đảm bảo dữ liệu được lưu trữ và truy xuất chính xác từ Redis.</p>
   * <p>
   * **Các thiết lập quan trọng:**
   * <ul>
   *   <li>📌 **Key serializer**: Dùng `StringRedisSerializer` để lưu key dưới dạng chuỗi.</li>
   *   <li>📌 **Value serializer**: Dùng `GenericJackson2JsonRedisSerializer` để hỗ trợ JSON.</li>
   *   <li>📌 **ObjectMapper**: Cấu hình để hỗ trợ Java 8 Date/Time API và đảm bảo serialization an toàn.</li>
   * </ul>
   *
   * @param connectionFactory Đối tượng `RedisConnectionFactory` dùng để kết nối Redis.
   * @return Đối tượng `RedisTemplate<String, Object>` đã được cấu hình.
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();

    // Cấu hình serializer cho key
    template.setKeySerializer(new StringRedisSerializer());

    // Cấu hình ObjectMapper cho việc chuyển đổi JSON
    var objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                           false); // Bỏ qua thuộc tính không xác định khi deserialize
    objectMapper.registerModule(new JavaTimeModule()); // Hỗ trợ Java 8 Date/Time API
    GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);

    // Kích hoạt Default Typing để lưu trữ kiểu dữ liệu trong Redis
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, As.PROPERTY);

    // Cấu hình serializer cho value
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    // Gán connection factory cho template
    template.setConnectionFactory(connectionFactory);

    return template;
  }
}
