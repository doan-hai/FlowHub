package com.flowhub.base.data;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;
import com.flowhub.base.security.UserPrincipal;
import com.flowhub.business.mapper.ModelMapper;

/**
 * **Lớp cơ sở cho các service (`BaseService`)**
 *
 * <p>Lớp này cung cấp các phương thức tiện ích dùng chung cho tất cả các service trong ứng
 * dụng.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các service khác có thể kế thừa `BaseService` để sử dụng các phương thức này mà không cần
 * viết lại logic.</p>
 * <p>
 * **📌 Ví dụ sử dụng `getCurrentUserId()` trong một service kế thừa:**
 * <pre>
 * {@code
 * @Service
 * public class UserService extends BaseService {
 *
 *     public String getCurrentUserEmail() {
 *         return getCurrentUserPrincipal().getEmail();
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
public abstract class BaseService {

  /**
   * **Đối tượng `ModelMapper` để ánh xạ dữ liệu giữa DTO và Entity**
   *
   * <p>Sử dụng `Mappers.getMapper(ModelMapper.class)` để khởi tạo một instance của
   * `ModelMapper`.</p>
   */
  protected ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);

  /**
   * **Lấy thông tin xác thực hiện tại (`Authentication`)**
   *
   * <p>Phương thức này lấy thông tin xác thực hiện tại từ `SecurityContextHolder`.
   * Dữ liệu này chứa các thông tin về người dùng đang đăng nhập.</p>
   * <p>
   * **📌 Ví dụ trả về đối tượng `Authentication`:**
   * <pre>
   * {@code
   * Authentication auth = getCurrentAuthentication();
   * }
   * </pre>
   *
   * @return Đối tượng `Authentication` chứa thông tin người dùng hiện tại.
   */
  protected Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * **Lấy ID của người dùng hiện tại**
   *
   * <p>Phương thức này trả về `username` hoặc `ID` của người dùng đang đăng nhập.
   * Nếu không tìm thấy người dùng, nó sẽ ném ngoại lệ `BaseException` với lỗi `UNAUTHORIZED`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Lấy `Authentication` hiện tại từ `SecurityContextHolder`.</li>
   *   <li>📌 Nếu có, trả về `authentication.getName()` (thường là username hoặc userId).</li>
   *   <li>📌 Nếu không, ném ngoại lệ `BaseException(CommonErrorDef.UNAUTHORIZED)`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String userId = getCurrentUserId();
   * System.out.println("User ID hiện tại: " + userId);
   * }
   * </pre>
   *
   * @return ID hoặc username của người dùng hiện tại.
   * @throws BaseException nếu người dùng chưa đăng nhập.
   */
  protected String getCurrentUserId() {
    var authentication = getCurrentAuthentication();
    if (authentication != null) {
      return authentication.getName();
    }
    throw new BaseException(CommonErrorDef.UNAUTHORIZED);
  }

  /**
   * **Lấy `UserPrincipal` của người dùng hiện tại**
   *
   * <p>Phương thức này trả về `UserPrincipal`, một đối tượng chứa đầy đủ thông tin
   * về người dùng đang đăng nhập (email, quyền hạn, vai trò, v.v.). Nếu không tìm thấy người dùng,
   * nó sẽ ném ngoại lệ `BaseException` với lỗi `UNAUTHORIZED`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Lấy `Authentication` hiện tại từ `SecurityContextHolder`.</li>
   *   <li>📌 Nếu có, ép kiểu `authentication.getPrincipal()` thành `UserPrincipal`.</li>
   *   <li>📌 Nếu không, ném ngoại lệ `BaseException(CommonErrorDef.UNAUTHORIZED)`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * UserPrincipal user = getCurrentUserPrincipal();
   * System.out.println("Email của người dùng: " + user.getEmail());
   * }
   * </pre>
   *
   * @return Đối tượng `UserPrincipal` của người dùng hiện tại.
   * @throws BaseException nếu người dùng chưa đăng nhập.
   */
  protected UserPrincipal getCurrentUserPrincipal() {
    var authentication = getCurrentAuthentication();
    if (authentication != null) {
      return (UserPrincipal) authentication.getPrincipal();
    }
    throw new BaseException(CommonErrorDef.UNAUTHORIZED);
  }
}
