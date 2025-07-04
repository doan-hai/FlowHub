package com.flowhub.base.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * **Lớp `ResponsePage` - Định dạng phản hồi phân trang cho API**
 *
 * <p>Lớp này được sử dụng để chuẩn hóa phản hồi từ các API có hỗ trợ phân trang.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi một API trả về danh sách dữ liệu có phân trang, thay vì chỉ trả về danh sách dữ liệu,
 * ta sử dụng `ResponsePage<T>` để cung cấp thêm thông tin về trang hiện tại, tổng số trang, và tổng
 * số bản ghi.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong API Controller:**
 * <pre>
 * {@code
 * @RestController
 * public class UserController {
 *
 *     @GetMapping("/users")
 *     public ResponseEntity<ResponsePage<UserDto>> getUsers(@RequestParam int pageNo, @RequestParam int pageSize) {
 *         Page<UserDto> users = userService.getAllUsers(PageRequest.of(pageNo - 1, pageSize));
 *         return ResponseEntity.ok(new ResponsePage<>(users));
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ JSON phản hồi của API có phân trang:**
 * <pre>
 * {
 *     "pageInfo": {
 *         "pageNo": 1,
 *         "pageSize": 20,
 *         "totalCount": 100,
 *         "totalPage": 5
 *     },
 *     "data": [
 *         {
 *             "id": 1,
 *             "name": "John Doe"
 *         },
 *         {
 *             "id": 2,
 *             "name": "Jane Doe"
 *         }
 *     ]
 * }
 * </pre>
 *
 * @param <T> Kiểu dữ liệu của danh sách phản hồi trong trang hiện tại.
 * @author haidv
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class ResponsePage<T> {

  @Schema(description = "Thông tin phân trang của dữ liệu")
  private PageInfo pageInfo;

  @Schema(description = "Danh sách dữ liệu của trang hiện tại")
  private List<T> data;

  /**
   * **Constructor khởi tạo `ResponsePage` từ thông tin phân trang riêng lẻ**
   *
   * <p>Phương thức này khởi tạo một `ResponsePage` từ thông tin phân trang thủ công.</p>
   *
   * @param pageNo     Số trang hiện tại.
   * @param pageSize   Số bản ghi trên mỗi trang.
   * @param totalCount Tổng số bản ghi.
   * @param totalPage  Tổng số trang.
   * @param data       Danh sách dữ liệu của trang hiện tại.
   */
  public ResponsePage(int pageNo, int pageSize, long totalCount, int totalPage, List<T> data) {
    this.pageInfo = new PageInfo(pageNo, pageSize, totalCount, totalPage);
    this.data = data;
  }

  /**
   * **Constructor khởi tạo `ResponsePage` từ đối tượng `Page<T>`**
   *
   * <p>Phương thức này giúp tạo `ResponsePage` từ `Page<T>` của Spring Data JPA.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Page<UserDto> users = userService.getAllUsers(PageRequest.of(0, 20));
   * ResponsePage<UserDto> response = new ResponsePage<>(users);
   * }
   * </pre>
   *
   * @param page Đối tượng `Page<T>` chứa thông tin phân trang và dữ liệu.
   */
  public ResponsePage(Page<T> page) {
    this(page, page.getContent());
  }

  /**
   * **Constructor hỗ trợ khởi tạo từ `Page<T>` và danh sách dữ liệu tùy chỉnh**
   *
   * <p>Phương thức này giúp khởi tạo `ResponsePage` khi muốn sử dụng danh sách dữ liệu
   * được biến đổi từ `Page<T>` ban đầu.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Page<UserEntity> users = userRepository.findAll(PageRequest.of(0, 20));
   * List<UserDto> userDtos = users.getContent().stream().map(userMapper::toDto).collect(Collectors.toList());
   * ResponsePage<UserDto> response = new ResponsePage<>(users, userDtos);
   * }
   * </pre>
   *
   * @param page Đối tượng `Page<T>` chứa thông tin phân trang.
   * @param data Danh sách dữ liệu tùy chỉnh (có thể đã qua ánh xạ DTO).
   */
  @SuppressWarnings("rawtypes")
  public ResponsePage(Page page, List<T> data) {
    this(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages(), data);
  }
}
