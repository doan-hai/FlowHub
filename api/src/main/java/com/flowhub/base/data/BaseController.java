package com.flowhub.base.data;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * **Lớp cơ sở cho các Controller (`BaseController`)**
 *
 * <p>Lớp này cung cấp các phương thức tiện ích dùng chung cho các Controller khác trong ứng
 * dụng.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các Controller khác có thể kế thừa `BaseController` để sử dụng trực tiếp các phương thức
 * này.</p>
 * <p>
 * **📌 Ví dụ sử dụng `validateInput`:**
 * <pre>
 * {@code
 * @RestController
 * public class UserController extends BaseController {
 *
 *     @PostMapping("/users")
 *     public ResponseEntity<?> createUser(@RequestBody @Valid UserDto userDto) {
 *         validateInput(userDto);
 *         userService.save(userDto);
 *         return ResponseEntity.ok("User created successfully");
 *     }
 * }
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public abstract class BaseController {

  /**
   * **Đối tượng Validator để kiểm tra tính hợp lệ của dữ liệu**
   *
   * <p>Spring sẽ tự động inject một instance của `Validator` vào đây.
   * Validator này được sử dụng để kiểm tra dữ liệu đầu vào của request.</p>
   */
  @Autowired
  private Validator validator;

  /**
   * **Phương thức kiểm tra tính hợp lệ của dữ liệu đầu vào (`validateInput`)**
   *
   * <p>Phương thức này sử dụng `Validator` của Jakarta để kiểm tra dữ liệu đầu vào.
   * Nếu dữ liệu không hợp lệ, nó sẽ ném `ConstraintViolationException` chứa danh sách lỗi.</p>
   *
   * @param input Đối tượng đầu vào cần kiểm tra.
   * @throws ConstraintViolationException Nếu dữ liệu không hợp lệ.
   */
  protected void validateInput(Object input) {
    Set<ConstraintViolation<Object>> violations = validator.validate(input);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  /**
   * **Phương thức hỗ trợ phân trang (`pageable`)**
   *
   * <p>Phương thức này giúp tạo `Pageable` để sử dụng trong truy vấn dữ liệu với phân trang.</p>
   * <p>
   * **📌 Cách xử lý tham số:**
   * <ul>
   *   <li>📌 `pageNo` (số trang): Nếu null hoặc nhỏ hơn 1, mặc định là trang đầu tiên (`1`).</li>
   *   <li>📌 `pageSize` (số bản ghi mỗi trang): Nếu null, nhỏ hơn 1 hoặc lớn hơn 200, mặc định là `200`.</li>
   *   <li>📌 `sort` (chuỗi sắp xếp): Nếu không có, sử dụng `Sort.unsorted()`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Pageable pageable = pageable(1, 50, "name:asc_age:desc");
   * }
   * </pre>
   *
   * @param pageNo   Số trang hiện tại (bắt đầu từ `1`).
   * @param pageSize Số bản ghi trên mỗi trang (giới hạn tối đa `200`).
   * @param sort     Chuỗi sắp xếp (ví dụ: `"name:asc_age:desc"`).
   * @return Đối tượng `Pageable` được tạo từ các tham số đầu vào.
   */
  protected Pageable pageable(Integer pageNo, Integer pageSize, String sort) {
    pageSize = (pageSize == null || pageSize <= 0 || pageSize > 200) ? 200 : pageSize;
    pageNo = (pageNo == null || pageNo <= 0) ? 1 : pageNo;
    Sort srt = this.getSort(sort);
    if (srt == null) {
      srt = Sort.unsorted();
    }
    return PageRequest.of(pageNo - 1, pageSize, srt);
  }

  /**
   * **Phương thức tạo đối tượng `Sort` từ chuỗi đầu vào (`getSort`)**
   *
   * <p>Phương thức này phân tích chuỗi sắp xếp đầu vào và tạo một `Sort` tương ứng.</p>
   * <p>
   * **📌 Định dạng chuỗi `sort`:** - Chuỗi gồm nhiều tiêu chí, ngăn cách nhau bởi dấu gạch dưới
   * `_`. - Mỗi tiêu chí gồm tên cột và hướng sắp xếp, ngăn cách bởi dấu hai chấm `:`. - Hướng sắp
   * xếp có thể là `asc` (tăng dần) hoặc `desc` (giảm dần).
   * <p>
   * **📌 Ví dụ chuỗi hợp lệ:**
   * <pre>
   * "name:asc_age:desc"  => Sắp xếp theo `name` tăng dần, sau đó theo `age` giảm dần.
   * "createdDate:desc"    => Sắp xếp theo `createdDate` giảm dần.
   * </pre>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * Sort sort = getSort("name:asc_age:desc");
   * }
   * </pre>
   *
   * @param sort Chuỗi mô tả tiêu chí sắp xếp.
   * @return Đối tượng `Sort` tương ứng hoặc `null` nếu không có sắp xếp hợp lệ.
   */
  private Sort getSort(String sort) {
    Sort srt = null;
    if (sort != null) {
      String[] part = sort.split("_"); // Tách các tiêu chí sắp xếp
      for (String s : part) {
        String[] tmp = s.split(":"); // Tách tên cột và hướng sắp xếp
        if (tmp.length == 2) {
          if (srt == null) {
            srt = Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim());
          } else {
            srt = srt.and(Sort.by(Sort.Direction.fromString(tmp[1].trim()), tmp[0].trim()));
          }
        }
      }
    }
    return srt;
  }
}
