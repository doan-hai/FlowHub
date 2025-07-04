package com.flowhub.base.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import com.flowhub.base.exception.BaseException;

/**
 * **Lớp `DrivenEventListener` - Lắng nghe và xử lý sự kiện trong hệ thống**
 *
 * <p>Lớp này đóng vai trò là một event listener trung gian, chịu trách nhiệm tiếp nhận sự kiện
 * và điều phối xử lý sự kiện đồng bộ hoặc bất đồng bộ.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Sự kiện sẽ được gửi vào phương thức `handleEvent()`. Dựa vào cấu hình, sự kiện sẽ:</p>
 * <ul>
 *   <li>📌 Được xử lý bất đồng bộ nếu `isSync = false` (đưa vào `TaskExecutor`).</li>
 *   <li>📌 Được xử lý đồng bộ nếu `isSync = true`.</li>
 *   <li>📌 Dựa vào thông tin trong `EventInfo`, hệ thống sẽ tìm `bean` và phương thức để xử lý sự kiện.</li>
 *   <li>📌 Nếu có lỗi xảy ra, hệ thống sẽ thực hiện lại (`retry`) theo số lần quy định.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ sử dụng sự kiện với `DrivenEventListener`:**
 * <pre>
 * {@code
 * @Component
 * public class UserEventHandler {
 *
 *     public void handleUserCreated(UserCreatedEvent event) {
 *         System.out.println("Handling user created event: " + event.getUserId());
 *     }
 * }
 * }
 * </pre>
 * <p>
 * **📌 Ví dụ gửi sự kiện:**
 * <pre>
 * {@code
 * EventInfo eventInfo = new EventInfo(new UserCreatedEvent(123), "userEventHandler", "handleUserCreated", false);
 * applicationContext.publishEvent(eventInfo);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public abstract class DrivenEventListener {

  /** ApplicationContext để lấy bean xử lý sự kiện** */
  private ApplicationContext applicationContext;

  /** Executor dùng để xử lý sự kiện bất đồng bộ** */
  private TaskExecutor executor;

  /**
   * **Constructor khởi tạo `DrivenEventListener`**
   *
   * @param applicationContext `ApplicationContext` dùng để lấy bean xử lý sự kiện.
   * @param executor           `TaskExecutor` để xử lý sự kiện bất đồng bộ.
   */
  protected DrivenEventListener(ApplicationContext applicationContext, TaskExecutor executor) {
    this.applicationContext = applicationContext;
    this.executor = executor;
  }

  /**
   * **Xử lý lỗi khi sự kiện gặp lỗi (`processHandleErrorEventAsync`)**
   *
   * <p>Phương thức này cần được các lớp con triển khai để xử lý khi có lỗi xảy ra.</p>
   *
   * @param eventInfo Thông tin sự kiện bị lỗi.
   */
  protected abstract void processHandleErrorEventAsync(EventInfo eventInfo);

  /**
   * **Ghi log khi xử lý sự kiện (`processLogHandleEventAsync`)**
   *
   * <p>Phương thức này cần được các lớp con triển khai để ghi log thông tin sự kiện.</p>
   *
   * @param eventInfo Thông tin sự kiện cần ghi log.
   */
  protected abstract void processLogHandleEventAsync(EventInfo eventInfo);

  /**
   * **Lắng nghe và xử lý sự kiện (`handleEvent`)**
   *
   * <p>Phương thức này sẽ tiếp nhận sự kiện từ hệ thống và xác định cách xử lý:</p>
   * <ul>
   *   <li>📌 Nếu `isSync = false`, sự kiện sẽ được xử lý bất đồng bộ bằng `TaskExecutor`.</li>
   *   <li>📌 Nếu `isSync = true`, sự kiện sẽ được xử lý ngay lập tức.</li>
   * </ul>
   *
   * @param eventInfo Thông tin sự kiện cần xử lý.
   */
  @EventListener
  public void handleEvent(EventInfo eventInfo) {
    if (!eventInfo.isSync()) {
      var context = ThreadContext.getContext();
      executor.execute(
          () -> {
            ThreadContext.putAll(context);
            processLogHandleEventAsync(eventInfo);
            routerEventHandle(eventInfo);
            ThreadContext.clearAll();
          });
    } else {
      routerEventHandle(eventInfo);
    }
  }

  /**
   * **Xác định bean và phương thức xử lý sự kiện (`routerEventHandle`)**
   *
   * <p>Phương thức này lấy thông tin về bean và phương thức xử lý sự kiện từ `EventInfo`
   * và thực thi phương thức đó.</p>
   *
   * @param eventInfo Thông tin sự kiện.
   */
  private void routerEventHandle(EventInfo eventInfo) {
    var event = eventInfo.getEvent();
    if (event == null) {
      log.warn("The event to be handled was not found");
      return;
    }
    log.info("Start handle event: {} id: {}", event.getEventName(), eventInfo.getId());
    var handleEventClassName = event.getHandleEventBeanName();
    var handleEventFunctionName = event.getHandleEventFunctionName();
    try {
      Object obj = applicationContext.getBean(handleEventClassName);
      var opt =
          Arrays.stream(obj.getClass().getMethods())
                .filter(v -> v.getName().equals(handleEventFunctionName))
                .findFirst();
      if (opt.isPresent()) {
        invokeHandleMethod(0, eventInfo.getRetry() + 1, opt.get(), obj, eventInfo);
      } else {
        log.warn("Method {} not found", handleEventFunctionName);
      }
    } catch (BeansException e) {
      log.warn("Bean {} not found", handleEventClassName, e);
    }
    log.info("End handle event: {} id: {}", event.getEventName(), eventInfo.getId());
  }

  /**
   * **Thực thi phương thức xử lý sự kiện (`invokeHandleMethod`)**
   *
   * <p>Phương thức này sẽ gọi phương thức xử lý sự kiện và hỗ trợ retry nếu có lỗi xảy ra.</p>
   * <p>
   * **📌 Cách xử lý khi có lỗi:**
   * <ul>
   *   <li>📌 Nếu số lần thử lại đạt giới hạn, gọi `processHandleErrorEventAsync`.</li>
   *   <li>📌 Nếu xảy ra `BaseException`, ném lỗi ngay lập tức.</li>
   *   <li>📌 Nếu xảy ra lỗi khác, thực hiện retry.</li>
   * </ul>
   *
   * @param index           Số lần thử hiện tại.
   * @param totalNumberExec Số lần thử tối đa.
   * @param m               Phương thức cần gọi.
   * @param obj             Đối tượng chứa phương thức.
   * @param eventInfo       Thông tin sự kiện.
   */
  private void invokeHandleMethod(
      int index, int totalNumberExec, Method m, Object obj, EventInfo eventInfo) {
    var event = eventInfo.getEvent();
    if (index >= totalNumberExec && !eventInfo.isSync()) {
      processHandleErrorEventAsync(eventInfo);
      return;
    }
    if (index > 0) {
      log.info("Try the {}th event handling {} again", index, event.getEventName());
    }
    try {
      m.invoke(obj, eventInfo.getWhat());
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof BaseException baseException) {
        throw baseException;
      }
      log.error("Handle event {} error", event.getEventName(), e);
      invokeHandleMethod(index + 1, totalNumberExec, m, obj, eventInfo);
    } catch (Exception e) {
      log.error("Handle event {} error", event.getEventName(), e);
      invokeHandleMethod(index + 1, totalNumberExec, m, obj, eventInfo);
    }
  }
}
