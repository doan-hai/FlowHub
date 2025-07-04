package com.flowhub.business;

import jakarta.annotation.PostConstruct;

import java.util.Random;
import java.util.TimeZone;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import com.flowhub.base.config.PersistenceConfig;
import com.flowhub.base.utils.Snowflake;

/**
 * The main entry point for the Spring Boot application. This class also handles some initial setup
 * such as setting the application name and default timezone.
 *
 * @author haidv
 * @version 1.0
 */
@ComponentScan(basePackages = "com.flowhub")
@EnableFeignClients(basePackages = "com.flowhub.business")
@ConfigurationPropertiesScan(basePackages = "com.flowhub")
@EnableJpaRepositories(basePackages = "com.flowhub.business")
@EnableSpringDataWebSupport
@Import({PersistenceConfig.class})
@SpringBootApplication
public class BusinessApplication {

  /** The name of the application, set during initialization. */
  @Setter
  private static String application = "";

  /** The name of the service, injected from the application properties. */
  @Value("${spring.application.name}")
  private String serviceName;

  private final Random random = new Random();

  /**
   * The main method, which starts the Spring Boot application.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(BusinessApplication.class, args);
  }

  /**
   * This method is called after dependency injection is done to perform any initialization. Here it
   * sets the application name, initializes the Snowflake instance and sets the default timezone.
   */
  @PostConstruct
  public void init() {
    BusinessApplication.setApplication(serviceName);
    Snowflake.getInstance(random.nextInt(1, 1024));
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
  }

  public static String getApplicationName() {
    return application;
  }
}
