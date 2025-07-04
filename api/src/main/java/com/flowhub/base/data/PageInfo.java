package com.flowhub.base.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * **Lớp chứa thông tin phân trang (`PageInfo`)**
 *
 * <p>Lớp này được sử dụng để lưu trữ thông tin về phân trang trong hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Lớp này thường được sử dụng trong API trả về dữ liệu phân trang để giúp client hiển thị dữ
 * liệu hợp lý.</p>
 * <p>
 * **📌 Ví dụ sử dụng trong API:**
 * <pre>
 * {@code
 * PageInfo pageInfo = new PageInfo(1, 20, 100, 5);
 * System.out.println("Trang hiện tại: " + pageInfo.getPageNo());
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ JSON phản hồi của API có phân trang:**
 * <pre>
 * {
 *     "pageNo": 1,
 *     "pageSize": 20,
 *     "totalCount": 100,
 *     "totalPage": 5
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public class PageInfo {

  @Schema(description = "Số trang hiện tại (bắt đầu từ 1)")
  private int pageNo;

  @Schema(description = "Số bản ghi trên mỗi trang")
  private int pageSize;

  @Schema(description = "Tổng số bản ghi trong kết quả tìm kiếm")
  private long totalCount;

  @Schema(description = "Tổng số trang cần thiết để hiển thị toàn bộ dữ liệu")
  private int totalPage;
}
