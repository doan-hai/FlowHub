package com.flowhub.base.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.flowhub.base.constant.DateConstant;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

/**
 * **Lớp `DateUtils` - Hỗ trợ xử lý định dạng ngày giờ**
 *
 * <p>Class tiện ích này cung cấp các `DateTimeFormatter` chuẩn
 * và các phương thức hỗ trợ liên quan đến thời gian.</p>
 * <p>
 * **📌 Ví dụ sử dụng `DateTimeFormatter`:**
 * <pre>
 * {@code
 * LocalDateTime now = LocalDateTime.now();
 * String formattedDate = now.format(DateUtils.YYYY_MM_DD_HH_MM_SS_FORMATTER);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
@Slf4j
public class DateUtils {

  private static final String DURATION_SECONDS = "duration.seconds";

  private static final String DURATION_MINUTES = "duration.minutes";

  private static final String DURATION_HOURS = "duration.hours";

  private static final String DURATION_DAYS = "duration.days";

  private static final Map<String, DateTimeFormatter> CONFIG_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_DASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DASH_FORMATTER;

  public static final DateTimeFormatter MM_YYYY_DASH_FORMATTER;

  public static final DateTimeFormatter MM_DD_YYYY_DASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_DASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_SLASH_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_SLASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_SLASH_FORMATTER;

  public static final DateTimeFormatter MM_YYYY_SLASH_FORMATTER;

  public static final DateTimeFormatter MM_DD_YYYY_SLASH_FORMATTER;

  public static final DateTimeFormatter HH_MM_FORMATTER;

  public static final DateTimeFormatter HH_MM_SS_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_HH_MM_DASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_DASH_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS_DASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_HH_MM_DASH_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SLASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SLASH_FORMATTER;

  public static final DateTimeFormatter DD_MM_YYYY_HH_MM_SS_SLASH_FORMATTER;

  public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SLASH_FORMATTER;

  private static final Map<String, Integer> MONTH_MAP = new HashMap<>();

  static {
    DD_MM_YYYY_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    MM_YYYY_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    MM_DD_YYYY_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('-')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_DASH_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  }

  static {
    YYYY_MM_DD_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    DD_MM_YYYY_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, 2)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    MM_YYYY_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    MM_DD_YYYY_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    HH_MM_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    HH_MM_SS_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_HH_MM_SS_FORMATTER =
        new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .append(HH_MM_SS_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    DD_MM_YYYY_HH_MM_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DD_MM_YYYY_DASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_HH_MM_SS_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(YYYY_MM_DD_DASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_SS_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    DD_MM_YYYY_HH_MM_SS_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DD_MM_YYYY_DASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_SS_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_HH_MM_DASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(YYYY_MM_DD_DASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    DD_MM_YYYY_HH_MM_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DD_MM_YYYY_SLASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_HH_MM_SS_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(YYYY_MM_DD_SLASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_SS_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    DD_MM_YYYY_HH_MM_SS_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DD_MM_YYYY_SLASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_SS_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    YYYY_MM_DD_HH_MM_SLASH_FORMATTER =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(YYYY_MM_DD_SLASH_FORMATTER)
            .appendLiteral(' ')
            .append(HH_MM_FORMATTER)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);
  }

  static {
    CONFIG_FORMATTER = new HashMap<>();
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_HH_MM_SS, YYYY_MM_DD_HH_MM_SS_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_DASH, DD_MM_YYYY_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DASH, YYYY_MM_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.MM_YYYY_DASH, MM_YYYY_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.MM_DD_YYYY_DASH, MM_DD_YYYY_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_DASH, YYYY_MM_DD_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_SLASH, YYYY_MM_DD_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_SLASH, DD_MM_YYYY_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_SLASH, YYYY_MM_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.MM_YYYY_SLASH, MM_YYYY_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.MM_DD_YYYY_SLASH, MM_DD_YYYY_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.HH_MM, HH_MM_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.HH_MM_SS, HH_MM_SS_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_HH_MM_DASH, DD_MM_YYYY_HH_MM_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_HH_MM_SS_DASH, YYYY_MM_DD_HH_MM_SS_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_HH_MM_SS_DASH, DD_MM_YYYY_HH_MM_SS_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_HH_MM_DASH, YYYY_MM_DD_HH_MM_DASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_HH_MM_SLASH, DD_MM_YYYY_HH_MM_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_HH_MM_SS_SLASH,
                         YYYY_MM_DD_HH_MM_SS_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.DD_MM_YYYY_HH_MM_SS_SLASH,
                         DD_MM_YYYY_HH_MM_SS_SLASH_FORMATTER);
    CONFIG_FORMATTER.put(DateConstant.YYYY_MM_DD_HH_MM_SLASH, YYYY_MM_DD_HH_MM_SLASH_FORMATTER);
  }

  static {
    // Khởi tạo danh sách tháng với cả viết tắt và đầy đủ
    String[][] months = {
        {"Jan", "January"}, {"Feb", "February"}, {"Mar", "March"},
        {"Apr", "April"}, {"May", "May"}, {"Jun", "June"},
        {"Jul", "July"}, {"Aug", "August"}, {"Sep", "September"},
        {"Oct", "October"}, {"Nov", "November"}, {"Dec", "December"}
    };
    for (int i = 0; i < months.length; i++) {
      for (String monthName : months[i]) {
        MONTH_MAP.put(monthName.toLowerCase(), i + 1);
      }
    }
  }

  /**
   * **Lấy danh sách định dạng ngày giờ từ cấu hình**
   *
   * @return Danh sách định dạng ngày giờ.
   */
  public static Map<String, DateTimeFormatter> getConfigFormatter() {
    return CONFIG_FORMATTER;
  }

  /**
   * **Xác định loại định dạng của pattern**
   *
   * @param pattern Chuỗi định dạng (vd: "yyyy-MM-dd HH:mm:ss", "HH:mm", "dd/MM/yyyy")
   * @return Loại định dạng: "date: 1", "time: 2", "datetime: 0"
   */
  public static int getFormatType(String pattern) {
    if (pattern == null || pattern.trim().isEmpty()) {
      throw new IllegalArgumentException("Pattern không được để trống!");
    }

    // Regex để kiểm tra sự tồn tại của ngày và thời gian trong pattern
    boolean hasDate = Pattern.compile("yyyy|MM|dd").matcher(pattern).find();
    boolean hasTime = Pattern.compile("HH|mm|ss").matcher(pattern).find();

    if (hasDate && hasTime) {
      return 0;
    } else if (hasDate) {
      return 1;
    } else if (hasTime) {
      return 2;
    } else {
      throw new IllegalArgumentException("Pattern không hợp lệ: " + pattern);
    }
  }

  /**
   * **Chuyển đổi giây thành chuỗi thời gian có định dạng (`formatDuration`)**
   *
   * <p>Phương thức này nhận số giây và trả về chuỗi biểu diễn thời gian với định dạng dễ đọc.
   * <p>
   * **📌 Quy tắc hiển thị:**
   * <pre>
   * - Dưới 1 phút: chỉ hiển thị giây (`30 seconds`)
   * - Từ 1 phút đến 1 giờ: hiển thị phút và giây (`5 minutes 30 seconds`)
   * - Từ 1 giờ đến 1 ngày: hiển thị giờ, phút và giây (`2 hours 15 minutes 10 seconds`)
   * - Trên 1 ngày: hiển thị ngày, giờ, phút và giây (`1 day 2 hours 10 minutes 5 seconds`)
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * System.out.println(DateUtils.formatDuration(3661));  // "1 hour 1 minute 1 second"
   * System.out.println(DateUtils.formatDuration(86400)); // "1 day"
   * }
   * </pre>
   *
   * @param seconds Số giây cần định dạng.
   * @return Chuỗi biểu diễn thời gian theo định dạng và ngôn ngữ mong muốn.
   * @throws IllegalArgumentException Nếu số giây nhỏ hơn 0.
   */
  public static String formatDuration(long seconds) {
    return formatDuration(seconds, RequestUtils.extractLocale());
  }

  /**
   * **Chuyển đổi giây thành chuỗi thời gian có định dạng (`formatDuration`)**
   *
   * <p>Phương thức này nhận số giây và trả về chuỗi biểu diễn thời gian với định dạng dễ đọc,
   * hỗ trợ đa ngôn ngữ bằng cách sử dụng `ResourceBundle`.</p>
   * <p>
   * **📌 Quy tắc hiển thị:**
   * <pre>
   * - Dưới 1 phút: chỉ hiển thị giây (`30 seconds`)
   * - Từ 1 phút đến 1 giờ: hiển thị phút và giây (`5 minutes 30 seconds`)
   * - Từ 1 giờ đến 1 ngày: hiển thị giờ, phút và giây (`2 hours 15 minutes 10 seconds`)
   * - Trên 1 ngày: hiển thị ngày, giờ, phút và giây (`1 day 2 hours 10 minutes 5 seconds`)
   * </pre>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * System.out.println(DateUtils.formatDuration(3661, Locale.ENGLISH)); // "1 hour 1 minute 1 second"
   * System.out.println(DateUtils.formatDuration(3661, new Locale("vi"))); // "1 giờ 1 phút 1 giây"
   * }
   * </pre>
   *
   * @param seconds Số giây cần định dạng.
   * @param locale  Ngôn ngữ hiển thị.
   * @return Chuỗi biểu diễn thời gian theo định dạng và ngôn ngữ mong muốn.
   * @throws IllegalArgumentException Nếu số giây nhỏ hơn 0.
   */
  public static String formatDuration(long seconds, Locale locale) {
    if (seconds < 0) {
      throw new IllegalArgumentException("Seconds cannot be negative");
    }
    ResourceBundle bundle = ResourceBundle.getBundle("lang/messages", locale);
    if (seconds < 60) {
      return formatSecondsOnly(seconds, bundle);
    } else if (seconds < 3600) { // Dưới 1 giờ, hiển thị phút + giây
      return formatMinutesAndSeconds(seconds, bundle);
    } else if (seconds < 86400) { // Dưới 1 ngày, hiển thị giờ + phút + giây
      return formatHoursMinutesSeconds(seconds, bundle);
    } else { // Từ 1 ngày trở lên, hiển thị ngày + giờ + phút + giây
      return formatDaysHoursMinutesSeconds(seconds, bundle);
    }
  }

  private static String formatSecondsOnly(long seconds, ResourceBundle bundle) {
    return seconds + StringUtils.SPACE + bundle.getString(DURATION_SECONDS);
  }

  private static String formatMinutesAndSeconds(long seconds, ResourceBundle bundle) {
    long minutes = seconds / 60;
    long remainingSeconds = seconds % 60;

    return (remainingSeconds == 0)
        ? minutes + StringUtils.SPACE + bundle.getString(DURATION_MINUTES)
        : minutes + StringUtils.SPACE + bundle.getString(DURATION_MINUTES) + StringUtils.SPACE
            + remainingSeconds + StringUtils.SPACE + bundle.getString(DURATION_SECONDS);
  }

  private static String formatHoursMinutesSeconds(long seconds, ResourceBundle bundle) {
    long hours = seconds / 3600;
    long remainingMinutes = (seconds % 3600) / 60;
    long remainingSeconds = seconds % 60;

    StringBuilder formatted = new StringBuilder();
    formatted.append(hours).append(StringUtils.SPACE).append(bundle.getString(DURATION_HOURS));

    if (remainingMinutes > 0) {
      formatted.append(StringUtils.SPACE)
               .append(remainingMinutes).append(StringUtils.SPACE)
               .append(bundle.getString(DURATION_MINUTES));
    }
    if (remainingSeconds > 0) {
      formatted.append(StringUtils.SPACE)
               .append(remainingSeconds).append(StringUtils.SPACE)
               .append(bundle.getString(DURATION_SECONDS));
    }

    return formatted.toString();
  }

  private static String formatDaysHoursMinutesSeconds(long seconds, ResourceBundle bundle) {
    long days = seconds / 86400;
    long remainingHours = (seconds % 86400) / 3600;
    long remainingMinutes = (seconds % 3600) / 60;
    long remainingSeconds = seconds % 60;

    StringBuilder formatted = new StringBuilder();
    formatted.append(days).append(StringUtils.SPACE).append(bundle.getString(DURATION_DAYS));

    if (remainingHours > 0) {
      formatted.append(StringUtils.SPACE)
               .append(remainingHours).append(StringUtils.SPACE)
               .append(bundle.getString(DURATION_HOURS));
    }
    if (remainingMinutes > 0) {
      formatted.append(StringUtils.SPACE)
               .append(remainingMinutes).append(StringUtils.SPACE)
               .append(bundle.getString(DURATION_MINUTES));
    }
    if (remainingSeconds > 0) {
      formatted.append(StringUtils.SPACE)
               .append(remainingSeconds).append(StringUtils.SPACE)
               .append(bundle.getString(DURATION_SECONDS));
    }

    return formatted.toString();
  }

  /**
   * **Chuyển đổi chuỗi thời gian thành định dạng khác (`convertFormat`)**
   *
   * <p>Phương thức này nhận vào chuỗi thời gian và định dạng cần chuyển đổi,
   * sau đó trả về chuỗi thời gian theo định dạng mới.</p>
   * <p>
   * **📌 Ví dụ sử dụng:**
   * <pre>
   * {@code
   * String date = "2022-12-31 23:59:59";
   * String formattedDate = DateUtils.convertFormat(date, DateConstant.YYYY_MM_DD_HH_MM_SS,
   *     DateConstant.DD_MM_YYYY_HH_MM_SS_DASH);
   * System.out.println(formattedDate); // "31-12-2022 23:59:59"
   * }
   * </pre>
   *
   * @param dateString Chuỗi thời gian cần chuyển đổi.
   * @param fromFormat Định dạng hiện tại của chuỗi thời gian.
   * @param toFormat   Định dạng mới cần chuyển đổi.
   * @return Chuỗi thời gian theo định dạng mới.
   */
  public static String convertFormat(String dateString, String fromFormat, String toFormat) {
    if (dateString == null || dateString.isBlank()) {
      throw new IllegalArgumentException("Date string cannot be null or empty");
    }

    DateTimeFormatter fromFormatter = CONFIG_FORMATTER.get(fromFormat);
    DateTimeFormatter toFormatter = CONFIG_FORMATTER.get(toFormat);

    if (fromFormatter == null || toFormatter == null) {
      throw new IllegalArgumentException("Invalid date format provided");
    }

    try {
      // Xác định kiểu dữ liệu: LocalDate, LocalTime, hoặc LocalDateTime
      if (fromFormat.contains("yyyy") && fromFormat.contains("HH")) {
        // Nếu định dạng có cả ngày và giờ → LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateString, fromFormatter);
        return dateTime.format(toFormatter);
      } else if (fromFormat.contains("yyyy")) {
        // Nếu chỉ có ngày → LocalDate
        LocalDate date = LocalDate.parse(dateString, fromFormatter);
        return date.format(toFormatter);
      } else {
        // Nếu chỉ có giờ → LocalTime
        LocalTime time = LocalTime.parse(dateString, fromFormatter);
        return time.format(toFormatter);
      }
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Failed to parse date/time: " + dateString, e);
    }
  }

  /**
   * **Chuyển đổi chuỗi ngày thành `LocalDate`**
   *
   * <p>Phương thức này tự động nhận diện định dạng của chuỗi ngày và chuyển đổi thành
   * `LocalDate`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Nếu chuỗi có dấu phân cách (`-`, `/`, `.` hoặc `,`), xử lý theo `parseDateWithSeparators()`
   * - Nếu chuỗi không có dấu phân cách, xử lý theo `parseDateWithoutSeparators()`
   * </pre>
   *
   * @param dateString Chuỗi ngày cần phân tích.
   * @return Đối tượng `LocalDate` tương ứng.
   * @throws DateTimeParseException Nếu không thể phân tích.
   */
  public static LocalDate parseDate(String dateString) {
    if (dateString == null || dateString.isBlank()) {
      throw new DateTimeParseException("Date string cannot be null or empty", StringUtils.EMPTY, 0);
    }

    // Kiểm tra nếu có dấu phân cách (`-`, `/`, `.`, `,`)
    if (dateString.contains("-") || dateString.contains("/") || dateString.contains(".")
        || dateString.contains(",") || containsMonthName(dateString)) {
      return parseDateWithSeparators(dateString);
    }

    // Nếu không có dấu phân cách, xử lý như một chuỗi số
    return parseDateWithoutSeparators(dateString);
  }

  /**
   * **Xử lý chuỗi ngày có dấu phân cách (`-`, `/`, `.`, `,`)**
   *
   * <p>Phương thức này hỗ trợ định dạng có tháng là chữ (Jan, February) hoặc số.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Nếu tháng là chữ (`15 Feb 2024` hoặc `2024-Feb-15`), chuyển thành số.
   * - Nếu ngày/tháng/năm đều là số (`15-02-2024`), kiểm tra thứ tự hợp lệ.
   * </pre>
   *
   * @param dateString Chuỗi ngày có dấu phân cách.
   * @return `LocalDate` sau khi phân tích.
   * @throws DateTimeParseException Nếu không thể phân tích.
   */
  private static LocalDate parseDateWithSeparators(String dateString) {
    // Thay thế dấu phân cách thành "-"
    dateString = dateString.replace("/", "-").replace(".", "-").replace(",", StringUtils.EMPTY);

    // Xử lý định dạng "dd MMM yyyy" (19 Feb 2024)
    Matcher matcher = Pattern.compile("(\\d{1,2})\\s*([a-zA-Z]+)\\s*(\\d{4})").matcher(dateString);
    if (matcher.find()) {
      int day = Integer.parseInt(matcher.group(1));
      String monthStr = matcher.group(2).toLowerCase();
      int year = Integer.parseInt(matcher.group(3));

      if (MONTH_MAP.containsKey(monthStr)) {
        int month = MONTH_MAP.get(monthStr);
        return LocalDate.of(year, month, day);
      }
    }

    // Xử lý định dạng "MMM dd yyyy" hoặc "MMMM dd yyyy" (Feb 19 2024, February 19 2024)
    matcher = Pattern.compile("([a-zA-Z]+)\\s*(\\d{1,2})\\s*(\\d{4})").matcher(dateString);
    if (matcher.find()) {
      String monthStr = matcher.group(1).toLowerCase();
      int day = Integer.parseInt(matcher.group(2));
      int year = Integer.parseInt(matcher.group(3));

      if (MONTH_MAP.containsKey(monthStr)) {
        int month = MONTH_MAP.get(monthStr);
        return LocalDate.of(year, month, day);
      }
    }

    // Xử lý định dạng "yyyy-MMM-dd" hoặc "yyyy-MMMM-dd" (2024-Feb-19)
    matcher = Pattern.compile("(\\d{4})\\s*([a-zA-Z]+)\\s*(\\d{1,2})").matcher(dateString);
    if (matcher.find()) {
      int year = Integer.parseInt(matcher.group(1));
      String monthStr = matcher.group(2).toLowerCase();
      int day = Integer.parseInt(matcher.group(3));

      if (MONTH_MAP.containsKey(monthStr)) {
        int month = MONTH_MAP.get(monthStr);
        return LocalDate.of(year, month, day);
      }
    }

    // Xử lý ngày dạng số (yyyy-MM-dd hoặc dd-MM-yyyy)
    String[] parts = dateString.split("-");
    if (parts.length == 3) {
      int part1 = Integer.parseInt(parts[0]);
      int part2 = Integer.parseInt(parts[1]);
      int part3 = Integer.parseInt(parts[2]);

      if (part1 > 31) {
        return LocalDate.of(part1, part2, part3); // yyyy-MM-dd
      }
      return LocalDate.of(part3, part2, part1); // dd-MM-yyyy
    }

    throw new DateTimeParseException("Invalid day format", dateString, 0);
  }

  /**
   * **Xử lý chuỗi ngày không có dấu phân cách**
   *
   * <p>Phương thức này nhận diện các định dạng như `yyyyMMdd`, `ddMMyyyy`, `yyyyMMd`, `ddMyyyy`,
   * ...</p>
   *
   * @param dateString Chuỗi không có dấu phân cách.
   * @return `LocalDate` sau khi phân tích.
   * @throws DateTimeParseException Nếu không thể phân tích.
   */
  private static LocalDate parseDateWithoutSeparators(String dateString) {
    int length = dateString.length();

    if (length == 8) {
      int firstPart = Integer.parseInt(dateString.substring(0, 4));
      int secondPart = Integer.parseInt(dateString.substring(4, 6));
      int thirdPart = Integer.parseInt(dateString.substring(6, 8));

      if (firstPart > 31 && secondPart <= 12) {
        return LocalDate.of(firstPart, secondPart, thirdPart); // yyyyMMdd
      } else if (firstPart > 31 && thirdPart <= 12) {
        return LocalDate.of(firstPart, thirdPart, secondPart); // yyyyddMM
      }
      firstPart = Integer.parseInt(dateString.substring(0, 2));
      secondPart = Integer.parseInt(dateString.substring(2, 4));
      thirdPart = Integer.parseInt(dateString.substring(4, 8));
      if (thirdPart > 31 && secondPart <= 12) {
        return LocalDate.of(thirdPart, secondPart, firstPart); // ddMMyyyy
      } else {
        return LocalDate.of(thirdPart, firstPart, secondPart); // MMddyyyy
      }
    }

    if (length == 7) {
      int firstPart = Integer.parseInt(dateString.substring(0, 4));
      int secondPart = Integer.parseInt(dateString.substring(4, 6));
      int thirdPart = Integer.parseInt(dateString.substring(6, 7));

      if (firstPart > 31) {
        return LocalDate.of(firstPart, secondPart, thirdPart); // yyyyMMd
      } else {
        return LocalDate.of(thirdPart, secondPart, firstPart); // dMyyyy
      }
    }

    throw new DateTimeParseException("Date format is not supported", dateString, 0);
  }

  /**
   * **Kiểm tra xem chuỗi có chứa tháng dạng chữ hay không**
   *
   * @param dateString Chuỗi ngày cần kiểm tra.
   * @return `true` nếu chứa tên tháng dạng chữ, `false` nếu không.
   */
  private static boolean containsMonthName(String dateString) {
    for (String month : MONTH_MAP.keySet()) {
      if (dateString.toLowerCase().contains(month)) {
        return true;
      }
    }
    return false;
  }


  /**
   * **Chuyển đổi `Object` thành `Temporal` (`LocalDate`, `LocalTime`, `LocalDateTime`)**
   *
   * @param obj Giá trị cần chuyển đổi.
   * @return `Temporal` tương ứng hoặc `null` nếu không thể chuyển đổi.
   */
  public static Temporal convertToTemporal(Object obj, String pattern) {
    if (obj instanceof Temporal temporal) {
      return temporal;
    }
    if (obj instanceof String dateString) {
      return parseTemporal(dateString, pattern);
    }
    return null;
  }

  /**
   * **Chuyển đổi `String` thành `Temporal`**
   *
   * @param dateStr Chuỗi ngày giờ cần parse.
   * @return `Temporal` tương ứng (`LocalDate`, `LocalTime`, `LocalDateTime`).
   */
  public static Temporal parseTemporal(String dateStr, String pattern) {
    try {
      int formatType = DateUtils.getFormatType(pattern);
      if (formatType == 0) {
        return LocalDateTime.parse(dateStr, DateUtils.CONFIG_FORMATTER.get(pattern));
      } else if (formatType == 1) {
        return LocalDate.parse(dateStr, DateUtils.CONFIG_FORMATTER.get(pattern));
      } else if (formatType == 2) {
        return LocalTime.parse(dateStr, DateUtils.CONFIG_FORMATTER.get(pattern));
      }
    } catch (DateTimeParseException e1) {
      log.error("Invalid date/time format: {}", dateStr);
    }
    return null;
  }
}
