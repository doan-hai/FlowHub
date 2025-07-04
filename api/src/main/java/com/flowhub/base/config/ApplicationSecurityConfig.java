package com.flowhub.base.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.exception.CustomAccessDeniedHandler;
import com.flowhub.base.exception.CustomAuthenticationEntryPoint;
import com.flowhub.base.security.AuthenticationFilter;

/**
 * **Cấu hình bảo mật cho ứng dụng (`ApplicationSecurityConfig`)**
 *
 * <p>Class này chịu trách nhiệm cấu hình bảo mật cho ứng dụng, bao gồm:</p>
 * <ul>
 *   <li>✅ Cấu hình xác thực và ủy quyền cho các API.</li>
 *   <li>✅ Cấu hình bộ lọc bảo mật (`SecurityFilterChain`).</li>
 *   <li>✅ Quản lý CORS để cho phép hoặc hạn chế các nguồn truy cập.</li>
 *   <li>✅ Xử lý các trường hợp từ chối truy cập và lỗi xác thực.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

  private final AuthenticationFilter jwtRequestFilter;

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  /**
   * **Cấu hình chuỗi bộ lọc bảo mật (`SecurityFilterChain`)**
   *
   * <p>Phương thức này thực hiện:</p>
   * <ul>
   *   <li>📌 Cho phép tất cả yêu cầu trong danh sách `IGNORE_AUTHENTICATION_PATTERN` đi qua.</li>
   *   <li>📌 Yêu cầu xác thực cho tất cả các yêu cầu còn lại.</li>
   *   <li>📌 Cấu hình xử lý lỗi khi truy cập bị từ chối (`CustomAccessDeniedHandler`).</li>
   *   <li>📌 Cấu hình xử lý lỗi khi xác thực thất bại (`CustomAuthenticationEntryPoint`).</li>
   *   <li>📌 Thêm bộ lọc JWT (`AuthenticationFilter`) vào trước bộ lọc xác thực `UsernamePasswordAuthenticationFilter`.</li>
   * </ul>
   *
   * @param http Đối tượng `HttpSecurity` để cấu hình bảo mật.
   * @return Đối tượng `SecurityFilterChain` đã cấu hình.
   * @throws Exception Nếu xảy ra lỗi khi thiết lập bảo mật.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      CorsConfiguration configuration = new CorsConfiguration();
                      configuration.setAllowedOrigins(List.of("*"));
                      configuration.setAllowedMethods(List.of("*"));
                      configuration.setAllowedHeaders(List.of("*"));
                      return configuration;
                    }))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers(RequestConstant.getIgnoreAuthenticationPattern())
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling
                    .accessDeniedHandler(customAccessDeniedHandler)
                    .authenticationEntryPoint(customAuthenticationEntryPoint));

    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /**
   * **Cấu hình `AuthenticationManager`**
   *
   * <p>Phương thức này trả về một `AuthenticationManager` được quản lý bởi Spring Security.</p>
   *
   * @param http Đối tượng `HttpSecurity` chứa thông tin bảo mật.
   * @return Một `AuthenticationManager` để quản lý xác thực người dùng.
   * @throws Exception Nếu có lỗi xảy ra khi khởi tạo.
   */
  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class).build();
  }

  /**
   * **Cấu hình `RequestContextListener`**
   *
   * <p>Phương thức này cung cấp một `RequestContextListener` để lắng nghe các sự kiện trong yêu
   * cầu HTTP.</p>
   *
   * @return Đối tượng `RequestContextListener`.
   */
  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  /**
   * **Cấu hình bộ mã hóa mật khẩu (`PasswordEncoder`)**
   *
   * <p>Phương thức này trả về một `BCryptPasswordEncoder` để mã hóa mật khẩu một cách an toàn.</p>
   *
   * @return Đối tượng `PasswordEncoder` sử dụng thuật toán BCrypt.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  /**
   * **Cấu hình xử lý endpoint Actuator**
   *
   * <p>Phương thức này tạo một `WebMvcEndpointHandlerMapping` để ánh xạ các endpoint của
   * Actuator.</p>
   *
   * @return Một `WebMvcEndpointHandlerMapping` để xử lý các endpoint Actuator.
   */
  @Bean
  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
      WebEndpointsSupplier webEndpointsSupplier,
      EndpointMediaTypes endpointMediaTypes,
      CorsEndpointProperties corsProperties,
      WebEndpointProperties webEndpointProperties,
      Environment environment) {
    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
    List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>(webEndpoints);
    String basePath = webEndpointProperties.getBasePath();
    EndpointMapping endpointMapping = new EndpointMapping(basePath);
    boolean shouldRegisterLinksMapping =
        this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
    return new WebMvcEndpointHandlerMapping(
        endpointMapping,
        webEndpoints,
        endpointMediaTypes,
        corsProperties.toCorsConfiguration(),
        new EndpointLinksResolver(allEndpoints, basePath),
        shouldRegisterLinksMapping);
  }

  /**
   * **Cấu hình CORS cho tài nguyên tĩnh**
   *
   * <p>Phương thức này giúp ánh xạ các tài nguyên tĩnh như Swagger UI, WebJars.</p>
   *
   * @return Một `WebMvcConfigurer` để cấu hình tài nguyên tĩnh.
   */
  @Bean
  public WebMvcConfigurer corsMappingConfigurer() {
    return new WebMvcConfigurer() {

      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry
            .addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");
        registry
            .addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
      }
    };
  }

  /**
   * **Xác định xem có nên đăng ký ánh xạ liên kết Actuator hay không**
   *
   * @return `true` nếu cần đăng ký ánh xạ, ngược lại `false`.
   */
  private boolean shouldRegisterLinksMapping(
      WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled()
        && (StringUtils.hasText(basePath)
        || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }
}
