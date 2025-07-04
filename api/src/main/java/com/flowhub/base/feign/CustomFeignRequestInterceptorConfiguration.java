package com.flowhub.base.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.utils.DateUtils;
import com.flowhub.base.utils.Snowflake;
import com.flowhub.business.BusinessApplication;

/**
 * **Lớp `CustomFeignRequestInterceptorConfiguration` - Cấu hình interceptor cho Feign Client**
 *
 * <p>Lớp này triển khai `RequestInterceptor` để can thiệp vào tất cả các request
 * được gửi từ Feign Client, giúp thêm các thông tin cần thiết vào header.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Mỗi khi Feign Client gửi một request, phương thức `apply(RequestTemplate template)`
 * sẽ được gọi để tự động chèn các thông tin vào header của request.</p>
 * <p>
 * **📌 Ví dụ header sau khi được thêm vào:**
 * <pre>
 * {
 *     "REQUEST_ID": "1234567890123456",
 *     "CLIENT_IP": "192.168.1.10",
 *     "CLIENT_TIME": "2025-02-14 12:34:56",
 *     "CLIENT_TIMEZONE": "MY_APP"
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Component
public class CustomFeignRequestInterceptorConfiguration implements RequestInterceptor {

  /**
   * **Interceptor Feign Client - Thêm thông tin vào request**
   *
   * <p>Phương thức này tự động thêm các thông tin vào header trước khi request
   * được gửi đi, giúp đảm bảo các request có đầy đủ metadata cần thiết.</p>
   *
   * @param template Đối tượng `RequestTemplate` chứa thông tin request.
   */
  @Override
  public void apply(RequestTemplate template) {
    String requestID;

    // **1️⃣ Kiểm tra và thiết lập REQUEST_ID**
    // Nếu request chưa có REQUEST_ID, tạo mới và thêm vào header
    if (CollectionUtils.isEmpty(template.headers().get(RequestConstant.REQUEST_ID))) {
      requestID = String.valueOf(Snowflake.getInstance().nextId());
      template.header(RequestConstant.REQUEST_ID, requestID);
    } else {
      // Nếu đã có REQUEST_ID, lấy giá trị đầu tiên
      requestID = (String) template.headers().get(RequestConstant.REQUEST_ID).toArray()[0];
    }

    // **Thêm REQUEST_ID vào ThreadContext để theo dõi logging**
    ThreadContext.put(RequestConstant.REQUEST_ID, requestID);

    try {
      // **2️⃣ Lấy địa chỉ IP của client và thêm vào header**
      template.header(RequestConstant.CLIENT_IP, InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      // Nếu không lấy được IP, sử dụng IP mặc định "127.0.0.1"
      template.header(RequestConstant.CLIENT_IP, "127.0.0.1");
    }

    // **3️⃣ Ghi nhận thời gian gửi request và thêm vào header**
    template.header(
        RequestConstant.CLIENT_TIME,
        LocalDateTime.now().format(DateUtils.YYYY_MM_DD_HH_MM_SS_FORMATTER));

    // **4️⃣ Thêm thông tin múi giờ vào header**
    template.header(RequestConstant.CLIENT_TIMEZONE, BusinessApplication.getApplicationName());
  }
}