package com.flowhub.base.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.TimeZone;
import lombok.Getter;
import lombok.ToString;
import com.flowhub.base.utils.DateUtils;
import com.flowhub.base.utils.RequestUtils;
import com.flowhub.business.BusinessApplication;

/**
 * **Lớp `ResponseData` - Định dạng phản hồi chung cho API**
 *
 * <p>Lớp này được sử dụng để định dạng phản hồi trả về từ API, giúp tạo sự nhất quán
 * giữa các API trong hệ thống.</p>
 * <p>
 * **📌 Cách sử dụng:**
 * <p>Lớp này có thể được sử dụng để trả về kết quả từ các API dưới dạng JSON.</p>
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
 *         return ResponseEntity.ok(new ResponseData<List<UserDto>>().success(users));
 *     }
 *
 *     @GetMapping("/error")
 *     public ResponseEntity<ResponseData<Void>> getError() {
 *         return ResponseEntity.badRequest().body(new ResponseData<Void>().error("400", "Bad Request"));
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> Kiểu dữ liệu của phản hồi API.
 * @author haidv
 * @version 1.0
 */
@ToString
@Getter
public class ResponseData<T> {

  @Schema(description = "Thời gian phản hồi của server")
  private final String serverTime;

  @Schema(description = "Thông tin múi giờ của máy chủ")
  private final String zoneInfo;

  @Schema(description = "Tên của dịch vụ đang xử lý yêu cầu")
  private final String service;

  @Schema(description = "ID của phiên làm việc (session)")
  private final String sessionId;

  @Schema(description = "ID của request để hỗ trợ tracking")
  private final String requestId;

  @Schema(description = "Mã phản hồi (0 = thành công, khác 0 = lỗi)")
  private String code;

  @Schema(description = "Thông báo phản hồi từ hệ thống")
  private String message;

  @Schema(description = "Mô tả chi tiết lỗi (nếu có)")
  private String errorDesc;

  @Schema(description = "Dữ liệu phản hồi")
  private T data;

  /**
   * **Constructor mặc định**
   *
   * <p>Khởi tạo đối tượng `ResponseData` với giá trị mặc định:</p>
   * <ul>
   *   <li>📌 `code = "0"` (thành công).</li>
   *   <li>📌 `serverTime` lấy từ thời gian hiện tại.</li>
   *   <li>📌 `zoneInfo` lấy múi giờ của hệ thống.</li>
   *   <li>📌 `message = "Successful!"`.</li>
   *   <li>📌 `service` lấy từ `BusinessApplication.getApplicationName()`.</li>
   *   <li>📌 `requestId` và `sessionId` được lấy từ `RequestUtils`.</li>
   * </ul>
   */
  public ResponseData() {
    this.code = "0";
    this.serverTime = LocalDateTime.now().format(DateUtils.DD_MM_YYYY_HH_MM_SS_DASH_FORMATTER);
    this.zoneInfo = TimeZone.getDefault().getID();
    this.message = "Successful!";
    this.service = BusinessApplication.getApplicationName();
    this.requestId = RequestUtils.extractRequestId();
    this.sessionId = RequestUtils.extractSessionId();
  }

  /**
   * **Phương thức thiết lập phản hồi thành công**
   *
   * <p>Cập nhật dữ liệu phản hồi và giữ nguyên thông tin thành công mặc định.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseData<String> response = new ResponseData<String>().success("Dữ liệu thành công");
   * }
   * </pre>
   *
   * @param data Dữ liệu phản hồi.
   * @return Đối tượng `ResponseData<T>` với dữ liệu được thiết lập.
   */
  public ResponseData<T> success(T data) {
    this.data = data;
    return this;
  }

  /**
   * **Phương thức thiết lập phản hồi lỗi (mã lỗi và thông báo)**
   *
   * <p>Cho phép đặt mã lỗi và thông báo lỗi.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseData<Void> errorResponse = new ResponseData<Void>().error("404", "Resource Not Found");
   * }
   * </pre>
   *
   * @param code    Mã lỗi.
   * @param message Thông báo lỗi.
   * @return Đối tượng `ResponseData<T>` với lỗi được thiết lập.
   */
  public ResponseData<T> error(String code, String message) {
    this.code = code;
    this.message = message;
    return this;
  }

  /**
   * **Phương thức thiết lập phản hồi lỗi đầy đủ**
   *
   * <p>Cho phép đặt mã lỗi, mô tả lỗi, thông báo lỗi và dữ liệu.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * ResponseData<String> errorResponse = new ResponseData<String>().error("500", "Internal Server Error", "Lỗi hệ thống", null);
   * }
   * </pre>
   *
   * @param code      Mã lỗi.
   * @param errorDesc Mô tả chi tiết lỗi.
   * @param message   Thông báo lỗi.
   * @param data      Dữ liệu phản hồi (nếu có).
   * @return Đối tượng `ResponseData<T>` với thông tin lỗi được thiết lập.
   */
  public ResponseData<T> error(String code, String errorDesc, String message, T data) {
    this.data = data;
    this.code = code;
    this.message = message;
    this.errorDesc = errorDesc;
    return this;
  }
}
