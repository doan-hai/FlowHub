package com.flowhub.base.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.flowhub.base.constant.RequestConstant;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;
import com.flowhub.base.redis.TokenBlackListRepository;
import com.flowhub.base.utils.JsonUtils;
import com.flowhub.base.utils.RequestUtils;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `AuthenticationFilter` - Xác thực JWT cho mỗi request**
 *
 * <p>Filter này xác thực JWT trong mỗi request HTTP, kiểm tra token có hợp lệ không
 * và thiết lập `SecurityContextHolder` nếu xác thực thành công.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Nếu request có JWT hợp lệ, hệ thống sẽ lấy thông tin từ token và thiết lập thông tin xác thực.
 * - Nếu token bị vô hiệu hóa, sẽ ném lỗi `BaseException` với `REFRESH_TOKEN_EXPIRED`.
 * - Nếu có lỗi xác thực, hệ thống sẽ trả về JSON chứa thông tin lỗi.
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  /** Token Provider để xử lý JWT** */
  private final TokenProvider tokenProvider;

  /** Repository lưu trữ danh sách token bị vô hiệu hóa** */
  private final TokenBlackListRepository tokenBlackListRepository;

  private final MessageSource messageSource;

  /**
   * **Xác thực request dựa trên JWT (`doFilterInternal`)**
   *
   * <p>Phương thức này thực hiện xác thực JWT, kiểm tra token có hợp lệ không,
   * kiểm tra xem token có bị vô hiệu hóa không và thiết lập thông tin xác thực vào
   * `SecurityContextHolder` nếu hợp lệ.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <pre>
   * - Ghi log thông tin request vào `ThreadContext` để theo dõi logging.
   * - Lấy JWT từ request bằng `getJwtFromRequest(request)`.
   * - Nếu request không nằm trong danh sách bỏ qua (`IGNORE_AUTHENTICATION_PATTERN`), tiếp tục xử lý.
   * - Giải mã JWT để lấy thông tin user (`tokenProvider.getClaimsFromRSAToken()`).
   * - Nếu token đã bị vô hiệu hóa (`tokenBlackListRepository.find(claims.getSubject())`), ném lỗi.
   * - Nếu hợp lệ, tạo đối tượng `UserPrincipal` và thiết lập vào `SecurityContextHolder`.
   * </pre>
   *
   * @param request     Request HTTP.
   * @param response    Response HTTP.
   * @param filterChain Chuỗi filter tiếp theo.
   * @throws ServletException Nếu có lỗi Servlet.
   * @throws IOException      Nếu có lỗi I/O.
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Ghi thông tin request vào ThreadContext
      var requestId = request.getHeader(RequestConstant.REQUEST_ID);
      ThreadContext.put(
          RequestConstant.REQUEST_ID,
          requestId == null ? String.valueOf(Snowflake.getInstance().nextId()) : requestId);
      ThreadContext.put(RequestConstant.CLIENT_IP, RequestUtils.extractClientIpAddress(request));
      ThreadContext.put(RequestConstant.LOCAL_IP, request.getLocalAddr());
      ThreadContext.put(RequestConstant.DEVICE_ID, request.getHeader(RequestConstant.DEVICE_ID));
      ThreadContext.put(RequestConstant.DEVICE_NAME,
                        request.getHeader(RequestConstant.DEVICE_NAME));
      ThreadContext.put(RequestConstant.DEVICE_TYPE,
                        request.getHeader(RequestConstant.DEVICE_TYPE));
      ThreadContext.put(RequestConstant.BROWSER_NAME,
                        request.getHeader(RequestConstant.BROWSER_NAME));
      ThreadContext.put(RequestConstant.APPLICATION_VERSION,
                        request.getHeader(RequestConstant.APPLICATION_VERSION));
      ThreadContext.put(RequestConstant.CLIENT_LANG,
                        request.getHeader(RequestConstant.CLIENT_LANG));

      // Đánh dấu thời gian bắt đầu xử lý request
      request.setAttribute(RequestConstant.REQUEST_TIME_START, System.currentTimeMillis());

      // Xóa thông tin xác thực cũ
      SecurityContextHolder.getContext().setAuthentication(null);

      // Lấy JWT từ request
      var jwt = getJwtFromRequest(request);
      if (StringUtils.hasText(jwt)
          && !RequestUtils.matches(request,
                                   Set.of(RequestConstant.getIgnoreAuthenticationPattern()))) {

        // Giải mã JWT
        var claims =
            tokenProvider.getClaimsFromRSAToken(jwt.substring(RequestConstant.BEARER_PREFIX.length()));

        // Kiểm tra token có bị vô hiệu hóa không
        ThreadContext.put(RequestConstant.SESSION_ID, claims.getSubject());
        if (tokenBlackListRepository.find(claims.getSubject()).isPresent()) {
          throw new BaseException(CommonErrorDef.REFRESH_TOKEN_EXPIRED);
        }

        // Tạo UserPrincipal và thiết lập vào SecurityContext
        var userDetails = new UserPrincipal(claims.getSubject(),
                                            new HashMap<>(),
                                            new ArrayList<>());
        var authentication =
            new UsernamePasswordAuthenticationToken(userDetails,
                                                    null,
                                                    userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (BaseException ex) {
      log.error("Could not set user authentication in security context", ex);
      response.setStatus(ex.getHttpStatus().value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      var writer = response.getWriter();
      writer.println(JsonUtils.toJson(ResponseUtils.error(
          ex.getErrorCode(),
          ex.getMessage(),
          messageSource.getMessage(
              ex.getErrorCode(),
              ex.getMessageArg(),
              ex.getMessage(),
              RequestUtils.extractLocale()),
          ex.getHttpStatus()).getBody()));
      return;
    }
    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    var bearerToken = request.getHeader(RequestConstant.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(RequestConstant.BEARER_PREFIX)) {
      return bearerToken;
    }
    return null;
  }
}