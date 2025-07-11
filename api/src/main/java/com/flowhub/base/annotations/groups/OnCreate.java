package com.flowhub.base.annotations.groups;

/**
 * **Interface đánh dấu nhóm kiểm tra hợp lệ khi tạo mới (OnCreate)**
 *
 * <p>Interface này được sử dụng để xác định **nhóm kiểm tra hợp lệ (validation group)** dành cho
 * các thao tác **tạo mới (create operation)**. Interface này **không chứa phương thức hay trường dữ
 * liệu** mà chỉ dùng để phân biệt các nhóm kiểm tra hợp lệ.</p>
 *
 * <h2>Công dụng</h2>
 * <p>Khi áp dụng vào các ràng buộc kiểm tra dữ liệu (validation constraints), interface này giúp
 * chỉ định rằng một ràng buộc chỉ có hiệu lực khi thực hiện thao tác **tạo mới** mà không ảnh hưởng
 * đến các thao tác **cập nhật (update)**.</p>
 *
 * <h2>Ví dụ sử dụng:</h2>
 * <p>Giả sử ta có một lớp `UserDTO` với quy tắc kiểm tra: Trường `id` bắt buộc có khi **tạo mới**
 * nhưng không cần khi **cập nhật**.</p>
 *
 * <pre>
 * {@code
 * import jakarta.validation.constraints.NotNull;
 * import jakarta.validation.constraints.Size;
 * import com.flowhub.base.annotations.groups.OnCreate;
 *
 * public class UserDTO {
 *
 *     @NotNull(groups = OnCreate.class, message = "ID không được để trống khi tạo mới người dùng")
 *     private Long id;
 *
 *     @NotNull(message = "Tên người dùng không được để trống")
 *     @Size(min = 5, max = 20, message = "Tên người dùng phải có độ dài từ 5 đến 20 ký tự")
 *     private String username;
 * }
 * }
 * </pre>
 *
 * <h2>Áp dụng `OnCreate` trong Controller:</h2>
 * <p>Khi gọi API tạo mới, ta chỉ định rằng các ràng buộc thuộc nhóm `OnCreate` sẽ được kiểm
 * tra.</p>
 * <pre>
 * {@code
 * @PostMapping("/users")
 * public ResponseEntity<String> createUser(@Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
 *     return ResponseEntity.ok("Người dùng được tạo thành công!");
 * }
 * }
 * </pre>
 *
 * <h2>Lợi ích khi sử dụng `OnCreate`:</h2>
 * <ul>
 *     <li>✅ Cho phép kiểm soát **các ràng buộc kiểm tra hợp lệ khác nhau** giữa **tạo mới** và **cập nhật**.</li>
 *     <li>✅ Giúp mã nguồn **dễ đọc, dễ mở rộng**, phù hợp với các hệ thống lớn.</li>
 *     <li>✅ Hỗ trợ dễ dàng phân loại các quy tắc kiểm tra hợp lệ theo từng loại thao tác.</li>
 * </ul>
 *
 * @author haidv
 * @version 1.0
 */
public interface OnCreate {

}
