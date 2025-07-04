package com.flowhub.base.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * **Cấu hình Kafka (`KafkaConfig`)**
 *
 * <p>Lớp này chịu trách nhiệm cấu hình Kafka cho ứng dụng. Nó bao gồm các thiết lập cho
 * **consumer**, **producer**, **Kafka template** và **header mapper**.</p>
 * <p>
 * **📌 Chức năng chính:**
 * <ul>
 *   <li>✅ Cấu hình consumer để lắng nghe các tin nhắn từ Kafka.</li>
 *   <li>✅ Cấu hình producer để gửi tin nhắn đến Kafka.</li>
 *   <li>✅ Cấu hình KafkaTemplate để hỗ trợ gửi tin nhắn.</li>
 *   <li>✅ Cấu hình Kafka header mapper để ánh xạ header của tin nhắn.</li>
 *   <li>✅ Cấu hình ThreadPool để xử lý các consumer.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
@EnableKafka
@Configuration
public class KafkaConfig {

  @Value("${custom.properties.kafka.bootstrap-servers}")
  private String bootstrapServerUrl;

  @Value("${custom.properties.messaging.kafka.consumer.batch}")
  private boolean isBatchConsumerNapasTranfer;

  @Value("${custom.properties.kafka.comsumer.max.timeout}")
  private int consumerTimeout;

  @Value("${custom.properties.messaging.kafka.consumer.number.of.message.in.batch}")
  private int maxBatchRecordNapasTranfer;

  @Value("${custom.properties.messaging.consumer.pool.size}")
  private int kafkaConsumerThreadPoolSize;

  @Value("${custom.properties.graceful.shutdown.messaging.consumer.wait.time.max}")
  private int waitTimeMax;

  @Value("${custom.properties.messaging.consumer.pool.thread.name.prefix}")
  private String threadNamePrefix;

  /**
   * **Tạo ThreadPool cho Kafka Consumer**
   *
   * <p>Phương thức này tạo một `ThreadPoolTaskExecutor` để quản lý các luồng xử lý Kafka
   * consumer.</p>
   *
   * @return Một `ThreadPoolTaskExecutor` để xử lý các consumer.
   */
  @Bean(name = "kafkaConsumerThreadPool")
  public ThreadPoolTaskExecutor messageProcessorExecutor() {
    ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
    exec.setCorePoolSize(kafkaConsumerThreadPoolSize);
    exec.setMaxPoolSize(kafkaConsumerThreadPoolSize);
    exec.setAllowCoreThreadTimeOut(true);
    exec.setWaitForTasksToCompleteOnShutdown(true);
    exec.setAwaitTerminationSeconds(waitTimeMax);
    exec.setThreadNamePrefix(threadNamePrefix);
    return exec;
  }

  /**
   * **Cấu hình Kafka Consumer**
   *
   * <p>Phương thức này tạo một `ConsumerFactory` chứa các cấu hình cần thiết để consumer có thể
   * lắng nghe Kafka.</p>
   *
   * @return Một `ConsumerFactory` cho Kafka Consumer.
   */
  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxBatchRecordNapasTranfer);
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.DEFAULT_ISOLATION_LEVEL, IsolationLevel.READ_COMMITTED);
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerTimeout);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  /**
   * **Cấu hình Kafka Listener Container Factory**
   *
   * <p>Phương thức này tạo một `ConcurrentKafkaListenerContainerFactory` để lắng nghe và xử lý tin
   * nhắn từ Kafka.</p>
   *
   * @return Một `ConcurrentKafkaListenerContainerFactory` cho Kafka Consumer.
   */
  @Bean("kafkaListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setBatchListener(isBatchConsumerNapasTranfer);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    factory.getContainerProperties().setListenerTaskExecutor(messageProcessorExecutor());
    return factory;
  }

  /**
   * **Cấu hình Header Mapper cho Kafka**
   *
   * <p>Phương thức này tạo một `KafkaHeaderMapper` để ánh xạ các header của tin nhắn Kafka.</p>
   *
   * @return Một `KafkaHeaderMapper` để ánh xạ header.
   */
  @Bean("kafkaBinderHeaderMapper")
  public KafkaHeaderMapper kafkaBinderHeaderMapper() {
    DefaultKafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();
    mapper.setMapAllStringsOut(true);
    return mapper;
  }

  /**
   * **Cấu hình Kafka Producer**
   *
   * <p>Phương thức này tạo một `ProducerFactory` chứa các cấu hình cần thiết để producer có thể
   * gửi tin nhắn đến Kafka.</p>
   *
   * @return Một `ProducerFactory` cho Kafka Producer.
   */
  @Bean
  public ProducerFactory<Object, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  /**
   * **Cấu hình KafkaTemplate**
   *
   * <p>Phương thức này tạo một `KafkaTemplate` để hỗ trợ gửi tin nhắn Kafka.</p>
   *
   * @return Một `KafkaTemplate` để gửi tin nhắn.
   */
  @Bean
  public KafkaTemplate<Object, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
