package com.flowhub.base.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.flowhub.base.constant.DateConstant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author haidv
 * @version 1.0
 */
class DateUtilsTest {

  // --- Test formatDuration ---
  @Test
  @DisplayName("Test formatDuration - dưới 1 phút")
  void testFormatDuration_UnderOneMinute() {
    assertEquals("30 seconds", DateUtils.formatDuration(30));
  }

  @Test
  @DisplayName("Test formatDuration - dưới 1 giờ")
  void testFormatDuration_UnderOneHour() {
    assertEquals("5 minutes", DateUtils.formatDuration(300)); // 5 phút
    assertEquals("5 minutes 30 seconds", DateUtils.formatDuration(330)); // 5 phút 30 giây
  }

  @Test
  @DisplayName("Test formatDuration - dưới 1 ngày")
  void testFormatDuration_UnderOneDay() {
    assertEquals("2 hours", DateUtils.formatDuration(7200)); // 2 giờ
    assertEquals("2 hours 15 minutes", DateUtils.formatDuration(8100)); // 2 giờ 15 phút
  }

  @Test
  @DisplayName("Test formatDuration - trên 1 ngày")
  void testFormatDuration_OverOneDay() {
    assertEquals("1 days", DateUtils.formatDuration(86400)); // 1 ngày
    assertEquals("1 days 2 hours", DateUtils.formatDuration(93600)); // 1 ngày 2 giờ
  }

  @Test
  @DisplayName("Test formatDuration - 0 giây")
  void testFormatDuration_ZeroSeconds() {
    assertEquals("0 seconds", DateUtils.formatDuration(0));
  }

  @Test
  @DisplayName("Test formatDuration - ngoại lệ giá trị âm")
  void testFormatDuration_NegativeValue() {
    assertThrows(IllegalArgumentException.class, () -> DateUtils.formatDuration(-1));
  }

  // --- Test convertFormat ---
  @Test
  @DisplayName("Test convertFormat - Định dạng ngày")
  void testConvertFormat_Date() {
    String input = "2024-02-19";
    String expected = "19/02/2024";
    assertEquals(expected,
                 DateUtils.convertFormat(input,
                                         DateConstant.YYYY_MM_DD_DASH,
                                         DateConstant.DD_MM_YYYY_SLASH));
  }

  @Test
  @DisplayName("Test convertFormat - Định dạng giờ")
  void testConvertFormat_Time() {
    String input = "14:30:00";
    String expected = "14:30";
    assertEquals(expected,
                 DateUtils.convertFormat(input, DateConstant.HH_MM_SS, DateConstant.HH_MM));
  }

  @Test
  @DisplayName("Test convertFormat - Định dạng ngày giờ")
  void testConvertFormat_DateTime() {
    String input = "2024-02-19 14:30:00";
    String expected = "19-02-2024 14:30";
    assertEquals(expected,
                 DateUtils.convertFormat(input,
                                         DateConstant.YYYY_MM_DD_HH_MM_SS,
                                         DateConstant.DD_MM_YYYY_HH_MM_DASH));
  }

  @Test
  @DisplayName("Test convertFormat - Lỗi định dạng không hợp lệ")
  void testConvertFormat_InvalidFormat() {
    String input = "invalid-date";
    assertThrows(IllegalArgumentException.class,
                 () -> DateUtils.convertFormat(input,
                                               DateConstant.YYYY_MM_DD_DASH,
                                               DateConstant.DD_MM_YYYY_SLASH));
  }

  // --- Test parseDate ---
  @Test
  @DisplayName("Test parseDate - Định dạng có dấu phân cách")
  void testParseDate_WithSeparators() {
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("19/02/2024"));
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("2024-02-19"));
  }

  @Test
  @DisplayName("Test parseDate - Định dạng không có dấu phân cách")
  void testParseDate_WithoutSeparators() {
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("20240219")); // yyyyMMdd
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("19022024")); // ddMMyyyy
  }

  @Test
  @DisplayName("Test parseDate - Định dạng tháng dạng chữ")
  void testParseDate_MonthAsText() {
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("19 Feb 2024"));
    assertEquals(LocalDate.of(2024, 2, 19), DateUtils.parseDate("Feb 19, 2024"));
  }

  @Test
  @DisplayName("Test parseDate - Định dạng không hợp lệ")
  void testParseDate_InvalidFormat() {
    assertThrows(DateTimeParseException.class, () -> DateUtils.parseDate("invalid-date"));
  }
}
