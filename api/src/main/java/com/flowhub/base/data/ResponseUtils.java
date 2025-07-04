package com.flowhub.base.data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * **Lớp tiện ích hỗ trợ tạo phản hồi HTTP (`ResponseUtils`)**
 *
 * <p>Lớp này cung cấp các phương thức tiện ích để tạo `ResponseEntity<ResponseData<T>>`
 * một cách nhất quán trong hệ thống API.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các phương thức trong lớp này giúp API trả về phản hồi nhất quán mà không cần viết lại logic
 * nhiều lần.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong API Controller:**
 * <pre>
 * {@code
 * @RestController
 * public class UserController {
 *
 *     @GetMapping("/users")
 *     public ResponseEntity<ResponseData<List<UserDto>>> getUsers() {
 *         List<UserDto> users = userService.getAllUsers();
 *         return ResponseUtils.success(users);
 *     }
 *
 *     @PostMapping("/users")
 *     public ResponseEntity<ResponseData<UserDto>> createUser(@RequestBody UserDto userDto) {
 *         UserDto createdUser = userService.createUser(userDto);
 *         return ResponseUtils.created(createdUser);
 *     }
 *
 *     @GetMapping("/error")
 *     public ResponseEntity<ResponseData<Void>> getError() {
 *         return ResponseUtils.error("400", "Invalid Request", "Bad Request", HttpStatus.BAD_REQUEST);
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public final class ResponseUtils {

  /**
   * **Constructor riêng tư để chặn khởi tạo lớp**
   *
   * <p>Lớp này chỉ chứa các phương thức tiện ích (`static`),
   * nên không cần khởi tạo đối tượng. Nếu gọi constructor, nó sẽ ném `IllegalStateException`.</p>
   */
  private ResponseUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * **Tạo phản hồi thành công không có dữ liệu (`success`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã HTTP 200 OK và không có dữ liệu.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<Void>> response = ResponseUtils.success();
   * }
   * </pre>
   *
   * @param <T> Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` với mã HTTP 200 OK.
   */
  public static <T> ResponseEntity<ResponseData<T>> success() {
    return success(null);
  }

  /**
   * **Tạo phản hồi thành công với dữ liệu (`success`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã HTTP 200 OK và dữ liệu đi kèm.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<String>> response = ResponseUtils.success("Thành công");
   * }
   * </pre>
   *
   * @param o   Dữ liệu trả về.
   * @param <T> Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` với mã HTTP 200 OK và dữ liệu.
   */
  public static <T> ResponseEntity<ResponseData<T>> success(T o) {
    return ResponseEntity.ok(new ResponseData<T>().success(o));
  }

  /**
   * **Tạo phản hồi khi tài nguyên được tạo (`created`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã HTTP 201 CREATED mà không có dữ liệu.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<Void>> response = ResponseUtils.created();
   * }
   * </pre>
   *
   * @param <T> Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` với mã HTTP 201 CREATED.
   */
  public static <T> ResponseEntity<ResponseData<T>> created() {
    return created(null);
  }

  /**
   * **Tạo phản hồi khi tài nguyên được tạo với dữ liệu (`created`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã HTTP 201 CREATED và dữ liệu đi kèm.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<UserDto>> response = ResponseUtils.created(new UserDto("John", "Doe"));
   * }
   * </pre>
   *
   * @param o   Dữ liệu trả về.
   * @param <T> Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` với mã HTTP 201 CREATED và dữ liệu.
   */
  public static <T> ResponseEntity<ResponseData<T>> created(T o) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseData<T>().success(o));
  }

  /**
   * **Tạo phản hồi lỗi (`error`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã lỗi HTTP và thông báo lỗi.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<Void>> response = ResponseUtils.error("400", "Invalid Data", "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST);
   * }
   * </pre>
   *
   * @param code      Mã lỗi.
   * @param errorDesc Mô tả chi tiết lỗi.
   * @param message   Thông báo lỗi.
   * @param status    Mã HTTP tương ứng.
   * @param <T>       Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` chứa thông tin lỗi.
   */
  public static <T> ResponseEntity<ResponseData<T>> error(
      String code, String errorDesc, String message, HttpStatus status) {
    return error(code, errorDesc, message, null, status);
  }

  /**
   * **Tạo phản hồi lỗi với dữ liệu đi kèm (`error`)**
   *
   * <p>Phương thức này trả về `ResponseEntity` với mã lỗi HTTP, thông báo lỗi và dữ liệu đi
   * kèm.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseEntity<ResponseData<String>> response = ResponseUtils.error("500", "Internal Server Error", "Lỗi hệ thống", "Chi tiết lỗi", HttpStatus.INTERNAL_SERVER_ERROR);
   * }
   * </pre>
   *
   * @param code      Mã lỗi.
   * @param errorDesc Mô tả chi tiết lỗi.
   * @param message   Thông báo lỗi.
   * @param data      Dữ liệu đi kèm (nếu có).
   * @param status    Mã HTTP tương ứng.
   * @param <T>       Kiểu dữ liệu của phản hồi.
   * @return `ResponseEntity<ResponseData<T>>` chứa thông tin lỗi và dữ liệu đi kèm.
   */
  public static <T> ResponseEntity<ResponseData<T>> error(
      String code, String errorDesc, String message, T data, HttpStatus status) {
    return ResponseEntity.status(status).body(getResponseDataError(code, errorDesc, message, data));
  }

  /**
   * **Tạo đối tượng `ResponseData` chứa thông tin lỗi (`getResponseDataError`)**
   *
   * @param code      Mã lỗi.
   * @param errorDesc Mô tả lỗi.
   * @param message   Thông báo lỗi.
   * @param data      Dữ liệu phản hồi (nếu có).
   * @param <T>       Kiểu dữ liệu phản hồi.
   * @return `ResponseData<T>` chứa thông tin lỗi.
   */
  public static <T> ResponseData<T> getResponseDataError(
      String code, String errorDesc, String message, T data) {
    return new ResponseData<T>().error(code, errorDesc, message, data);
  }
}
