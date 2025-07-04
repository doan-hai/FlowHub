package com.flowhub.base.data;

import jakarta.persistence.Column;

import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * **PagingParameter**
 *
 * <p>Class này đại diện cho các tham số phân trang và sắp xếp dữ liệu.</p>
 *
 * <pre>
 * 📌 Chức năng chính:
 * - Cung cấp các tham số cơ bản cho phân trang như `pageSize`, `pageNo`, `sort`.
 * - Hỗ trợ chuyển đổi thành `Pageable` để sử dụng với Spring Data JPA.
 * - Hỗ trợ lấy tên cột tương ứng khi sử dụng native query.
 * </pre>
 * <p>
 * **📌 Cách sử dụng:**
 * <pre>
 * {@code
 * PagingParameter pagingParameter = new PagingParameter();
 * Pageable pageable = pagingParameter.pageable();
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Getter
@Setter
public class PagingParameter {

  @Schema(example = "20", description = "Số lượng bản ghi trên mỗi trang")
  protected Integer pageSize = 20;

  @Schema(example = "1", description = "Số trang (bắt đầu từ 1)")
  protected Integer pageNo = 1;

  @Schema(example = "createdAt:desc", description = "Sắp xếp dữ liệu")
  protected String sort = BaseEntity.Fields.createdAt + ":" + Sort.Direction.DESC;

  public Pageable pageable() {
    return pageable(false);
  }

  public Pageable pageable(boolean nativeQuery) {
    return pageable(nativeQuery, BaseEntity.class);
  }

  /**
   * **Tạo đối tượng `Pageable` với class entity**
   *
   * @param nativeQuery Nếu `true`, sử dụng tên cột trong database thay vì tên thuộc tính trong
   *                    entity.
   * @param clazz       Class của entity để lấy thông tin trường khi nativeQuery = true.
   * @return Đối tượng `Pageable` với thông tin phân trang và sắp xếp.
   */
  public Pageable pageable(boolean nativeQuery, Class<?> clazz) {
    pageSize = (pageSize == null || pageSize <= 0) ? 200 : pageSize;
    pageNo = (pageNo == null || pageNo <= 0) ? 1 : pageNo;
    Sort srt = this.getSort(sort, nativeQuery, clazz);
    if (srt == null) {
      srt = Sort.unsorted();
    }
    return PageRequest.of(pageNo - 1, pageSize, srt);
  }

  /**
   * **Chuyển đổi chuỗi `sort` thành `Sort` object**
   *
   * @param sort        Chuỗi định nghĩa sắp xếp, ví dụ: `createdAt:desc`.
   * @param nativeQuery Nếu `true`, lấy tên cột trong database thay vì tên thuộc tính.
   * @param clazz       Class của entity để lấy thông tin trường khi nativeQuery = true.
   * @return Đối tượng `Sort` chứa thông tin sắp xếp.
   */
  private Sort getSort(String sort, boolean nativeQuery, Class<?> clazz) {
    if (StringUtils.isEmpty(sort)) {
      return null;
    }
    Sort srt = null;
    String[] part = sort.split("_");
    for (String s : part) {
      String[] tmp = s.split(":");
      String property = tmp[0].trim();
      String direction = tmp[1].trim();
      if (tmp.length == 2) {
        String column = nativeQuery ? getColumnName(clazz, property) : property;
        if (srt == null) {
          srt =
              Sort.by(
                  Sort.Direction.fromString(direction), column);
        } else {
          srt.and(
              Sort.by(
                  Sort.Direction.fromString(direction), column));
        }
      }
    }
    return srt;
  }

  /**
   * **Lấy tên cột trong database từ tên thuộc tính của entity**
   *
   * @param clazz     Class của entity.
   * @param fieldName Tên thuộc tính cần lấy tên cột.
   * @return Tên cột trong database nếu có annotation `@Column`, ngược lại trả về chính `fieldName`.
   * @throws IllegalArgumentException Nếu không tìm thấy field trong entity.
   */
  private String getColumnName(Class<?> clazz, String fieldName) {
    Field field = findField(clazz, fieldName);
    if (field == null) {
      throw new IllegalArgumentException("Field không tồn tại: " + fieldName);
    }
    Column column = field.getAnnotation(Column.class);
    return (column != null)
        ? column.name()
        : fieldName; // Nếu không có @Column, dùng chính tên field
  }

  /**
   * **Tìm kiếm field trong class hoặc các class cha**
   *
   * @param clazz     Class của entity.
   * @param fieldName Tên thuộc tính cần tìm.
   * @return Đối tượng `Field` nếu tìm thấy, ngược lại trả về `null`.
   */
  private Field findField(Class<?> clazz, String fieldName) {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    return null;
  }
}
