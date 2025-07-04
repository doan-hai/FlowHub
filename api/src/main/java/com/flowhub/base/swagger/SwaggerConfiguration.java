package com.flowhub.base.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.utils.Snowflake;
import com.flowhub.business.BusinessApplication;

/**
 * **Lớp `SwaggerConfiguration` - Cấu hình Swagger cho API**
 *
 * <p>Lớp này cấu hình Swagger OpenAPI 3.0 cho hệ thống, giúp tạo tài liệu API tự động
 * và tùy chỉnh các thông tin header mặc định khi sử dụng Swagger UI.</p>
 * <pre>
 * **📌 Cách hoạt động:**
 * - Khi Swagger UI hoặc OpenAPI được khởi tạo, hệ thống sẽ sử dụng các Bean
 *   trong lớp này để thiết lập tài liệu API.
 * - `customGlobalHeaders()` giúp thêm các header mặc định vào mỗi API.
 * - `customOpenAPI()` định nghĩa các thông tin chung của API và cấu hình bảo mật.
 * </pre>
 * **📌 Ví dụ một request header được tự động thêm vào API:**
 * <pre>
 * {@code
 * GET /api/v1/resource
 * Headers:
 *   REQUEST_ID: 1234567890
 *   CLIENT_IP: 192.168.1.1
 *   CLIENT_TIME: 1700000000000
 *   APPLICATION_VERSION: 1.0.0
 *   AUTHORIZATION: Bearer abc123xyz
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(
    servers = {@Server(url = "${server.servlet.context-path}", description = "Default Server URL")})
public class SwaggerConfiguration {

  /**
   * **Thêm các header mặc định vào tất cả API (`customGlobalHeaders`)**
   *
   * <p>Phương thức này tự động thêm các thông tin vào header của mỗi API khi sử dụng Swagger
   * UI.</p>
   * <p>
   * **📌 Header được thêm vào:**
   * <pre>
   * - `REQUEST_ID`: ID request (tạo tự động bằng `Snowflake`).
   * - `CLIENT_IP`: Địa chỉ IP của client.
   * - `CLIENT_TIME`: Thời gian gửi request (timestamp).
   * - `CLIENT_TIMEZONE`: Múi giờ của client.
   * - `DEVICE_ID`: ID của thiết bị (tùy chọn).
   * - `DEVICE_NAME`: Tên của thiết bị (tùy chọn).
   * - `APPLICATION_VERSION`: Phiên bản ứng dụng.
   * - `DEVICE_TYPE`: Loại thiết bị (tùy chọn).
   * - `CLIENT_LANG`: Ngôn ngữ của client (tùy chọn).
   * </pre>
   *
   * @return `OperationCustomizer` giúp thêm header vào tất cả API.
   */
  @Bean
  public OperationCustomizer customGlobalHeaders() {
    return (Operation operation, HandlerMethod handlerMethod) -> {
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.REQUEST_ID)
              .example(Snowflake.getInstance().nextId())
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_IP)
              .example("127.0.0.1")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_TIME)
              .example(System.currentTimeMillis())
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_TIMEZONE)
              .example("Asia/Ho_Chi_Minh")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_ID)
              .example(Snowflake.getInstance().nextId())
              .required(false));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_NAME)
              .example("HAIDV")
              .required(false));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.APPLICATION_VERSION)
              .example("1.0.0")
              .required(true));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.DEVICE_TYPE)
              .example("MacOS")
              .required(false));
      operation.addParametersItem(
          new Parameter()
              .in(ParameterIn.HEADER.toString())
              .schema(new StringSchema())
              .name(RequestConstant.CLIENT_LANG)
              .example("vi")
              .required(false));

      return operation;
    };
  }

  /**
   * **Cấu hình chung cho OpenAPI (`customOpenAPI`)**
   *
   * <p>Phương thức này thiết lập thông tin chung cho tài liệu API và cấu hình bảo mật.</p>
   * <p>
   * **📌 Chức năng:** - Thiết lập tên API và phiên bản từ `BusinessApplication.getApplicationName()`. -
   * Định nghĩa API Key (`Authorization`) trong header để bảo mật API.
   * <p>
   * **📌 Cấu hình bảo mật:**
   * <pre>
   * {@code
   * securitySchemes:
   *   Authorization:
   *     type: apiKey
   *     in: header
   *     name: Authorization
   * }
   * </pre>
   *
   * @return Đối tượng `OpenAPI` chứa thông tin và cấu hình bảo mật của API.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title(BusinessApplication.getApplicationName()).version("1.0.0"))
        .components(
            new Components()
                .addSecuritySchemes(
                    RequestConstant.AUTHORIZATION,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(RequestConstant.AUTHORIZATION)))
        .addSecurityItem(new SecurityRequirement().addList(RequestConstant.AUTHORIZATION));
  }
}