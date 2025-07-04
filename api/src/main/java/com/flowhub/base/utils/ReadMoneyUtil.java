package com.flowhub.base.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * **Lớp `ReadMoneyUtil` - Chuyển đổi số tiền thành chữ tiếng Việt**
 *
 * <p>Phương thức `convertToWords` nhận vào một số dưới dạng chuỗi và trả về chuỗi
 * đọc số tiền tương ứng bằng tiếng Việt.</p>
 * <p>
 * **📌 Các quy tắc xử lý số tiền:**
 * <pre>
 * - Hàng **trăm, chục, đơn vị** được xử lý theo cách đọc chuẩn của tiếng Việt.
 * - Các đơn vị tiền tệ: **nghìn, triệu, tỷ** được thêm vào đúng vị trí.
 * - **Xử lý đặc biệt cho số 1, 5, 0** để đảm bảo đọc đúng ngữ pháp.
 * - **Bỏ qua các nhóm 000** không cần thiết.
 * </pre>
 * **📌 Ví dụ:**
 * <pre>
 * {@code
 * System.out.println(ReadMoneyUtil.convertToWords("1234567"));
 * // Output: "Một triệu hai trăm ba mươi tư nghìn năm trăm sáu mươi bảy đồng"
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@UtilityClass
@Slf4j
public class ReadMoneyUtil {

  public static String convertToWords(String input) {
    try {
      long total = Long.parseLong(input);
      StringBuilder rs = new StringBuilder();
      String[] ch = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
      String[] rch = {"linh", "mốt", "", "", "", "lăm"};
      String[] u = {"", "mươi", "trăm", "nghìn", "", "", "triệu", "", "", "tỷ", "", "", "nghìn", "",
                    "", "triệu"};
      String nstr = String.valueOf(total);
      long[] n = new long[nstr.length()];
      int len = n.length;
      for (int i = 0; i < len; i++) {
        n[len - 1 - i] = Long.parseLong(nstr.substring(i, i + 1));
      }
      for (int i = len - 1; i >= 0; i--) {
        if (i % 3 == 2)// số 0 ở hàng trăm
        {
          if (n[i] == 0 && n[i - 1] == 0 && n[i - 2] == 0) {
            continue;//nếu cả 3 số là 0 thì bỏ qua không đọc
          }
        } else if (i % 3 == 1) // số ở hàng chục
        {
          if (n[i] == 0) {
            if (n[i - 1] == 0) {
              continue;
            }// nếu hàng chục và hàng đơn vị đều là 0 thì bỏ qua.
            else {
              rs.append(" ").append(rch[(int) n[i]]);
              continue;// hàng chục là 0 thì bỏ qua, đọc số hàng đơn vị
            }
          }
          if (n[i] == 1)//nếu số hàng chục là 1 thì đọc là mười
          {
            rs.append(" mười");
            continue;
          }
        } else if (i != len - 1)// số ở hàng đơn vị (không phải là số đầu tiên)
        {
          if (n[i] == 0)// số hàng đơn vị là 0 thì chỉ đọc đơn vị
          {
            if (i + 2 <= len - 1 && n[i + 2] == 0 && n[i + 1] == 0) {
              continue;
            }
            rs.append(" ").append(u[i]);
            continue;
          }
          if (n[i] == 1)// nếu là 1 thì tùy vào số hàng chục mà đọc: 0,1: một / còn lại: mốt
          {
            rs.append(" ")
              .append((n[i + 1] == 1 || n[i + 1] == 0) ? ch[(int) n[i]] : rch[(int) n[i]]);
            rs.append(" ").append(u[i]);
            continue;
          }
          if (n[i] == 5 && n[i + 1] != 0) // cách đọc số 5 //nếu số hàng chục khác 0 thì đọc số 5 là lăm
          {
              rs.append(" ").append(rch[(int) n[i]]);// đọc số
              rs.append(" ").append(u[i]);// đọc đơn vị
              continue;
          }
        }
        rs.append(" ").append(ch[(int) n[i]]);// đọc số
        rs.append(" ").append(i % 3 == 0 ? u[i] : u[i % 3]);// đọc đơn vị
      }
      if (rs.charAt(rs.length() - 1) != ' ') {
        rs.append(" đồng");
      } else {
        rs.append("đồng");
      }

      if (rs.length() > 2) {
        String rs1 = rs.substring(0, 2);
        rs1 = rs1.toUpperCase();
        rs = new StringBuilder(rs.substring(2));
        rs.insert(0, rs1);
      }
      return rs.toString()
               .trim().replace("linh,", "linh")
               .replace("mươi,", "mươi")
               .replace("trăm,", "trăm")
               .replace("mười,", "mười");
    } catch (Exception ex) {
      log.error("Error when read money: ", ex);
      return StringUtils.EMPTY;
    }
  }
}
