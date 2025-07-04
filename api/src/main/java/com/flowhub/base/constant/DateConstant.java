package com.flowhub.base.constant;

/**
 * **Lớp chứa các định dạng ngày giờ phổ biến trong hệ thống (`DateConstant`)**
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Các hằng số trong lớp này có thể được sử dụng trực tiếp trong các thao tác xử lý ngày giờ
 * như định dạng (`format`), chuyển đổi (`parse`), hiển thị (`display`) hoặc lưu trữ (`store`).</p>
 * <p>
 * **📌 Ví dụ sử dụng `DateTimeFormatter`:**
 * <pre>
 * {@code
 * LocalDateTime now = LocalDateTime.now();
 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstant.YYYY_MM_DD_HH_MM_SS);
 * String formattedDate = now.format(formatter);
 * System.out.println("Ngày giờ hiện tại: " + formattedDate);
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ sử dụng `SimpleDateFormat`:**
 * <pre>
 * {@code
 * SimpleDateFormat sdf = new SimpleDateFormat(DateConstant.DD_MM_YYYY_DASH);
 * String formattedDate = sdf.format(new Date());
 * System.out.println("Ngày hiện tại: " + formattedDate);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
public class DateConstant {

  public static final String YYYY_MM_DD_HH_MM_SS = "yyyyMMddHHmmss";

  public static final String DD_MM_YYYY_DASH = "dd-MM-yyyy";

  public static final String YYYY_MM_DASH = "yyyy-MM";

  public static final String MM_YYYY_DASH = "MM-yyyy";

  public static final String MM_DD_YYYY_DASH = "MM-dd-yyyy";

  public static final String YYYY_MM_DD_DASH = "yyyy-MM-dd";

  public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";

  public static final String DD_MM_YYYY_SLASH = "dd/MM/yyyy";

  public static final String YYYY_MM_SLASH = "yyyy/MM";

  public static final String MM_YYYY_SLASH = "MM/yyyy";

  public static final String MM_DD_YYYY_SLASH = "MM/dd/yyyy";

  public static final String HH_MM = "HH:mm";

  public static final String HH_MM_SS = "HH:mm:ss";

  public static final String DD_MM_YYYY_HH_MM_DASH = "dd-MM-yyyy HH:mm";

  public static final String YYYY_MM_DD_HH_MM_SS_DASH = "yyyy-MM-dd HH:mm:ss";

  public static final String DD_MM_YYYY_HH_MM_SS_DASH = "dd-MM-yyyy HH:mm:ss";

  public static final String YYYY_MM_DD_HH_MM_DASH = "yyyy-MM-dd HH:mm";

  public static final String DD_MM_YYYY_HH_MM_SLASH = "dd/MM/yyyy HH:mm";

  public static final String YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";

  public static final String DD_MM_YYYY_HH_MM_SS_SLASH = "dd/MM/yyyy HH:mm:ss";

  public static final String YYYY_MM_DD_HH_MM_SLASH = "yyyy/MM/dd HH:mm";

  private DateConstant() {
    throw new IllegalStateException("Utility class");
  }
}
