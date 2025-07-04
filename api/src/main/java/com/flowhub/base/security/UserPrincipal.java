package com.flowhub.base.security;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

/**
 * **Lớp `UserPrincipal` - Đại diện cho người dùng trong hệ thống bảo mật**
 *
 * <p>Lớp này triển khai `UserDetails` của Spring Security để đại diện cho
 * thông tin của người dùng đã xác thực trong hệ thống.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <pre>
 * - Khi một người dùng đăng nhập, thông tin của họ sẽ được ánh xạ vào `UserPrincipal`.
 * - Danh sách quyền (`permissions`) được chuyển đổi thành `SimpleGrantedAuthority`.
 * - `getUsername()` lấy tên người dùng từ `userDatas`.
 * </pre>
 * <p>
 * **📌 Ví dụ sử dụng:**
 * <pre>
 * {@code
 * Map<String, Object> userInfo = Map.of("username", "johndoe", "email", "john@example.com");
 * UserPrincipal user = new UserPrincipal("session-123", userInfo, List.of("ROLE_ADMIN"));
 * System.out.println(user.getUsername()); // johndoe
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Getter
public class UserPrincipal implements UserDetails {

  private final String sessionId;

  private final Map<String, Object> userDatas;

  private Collection<SimpleGrantedAuthority> authorities;

  public UserPrincipal(String sessionId, Map<String, Object> userDatas,
                       Collection<String> permissions) {
    this.sessionId = sessionId;
    this.userDatas = userDatas;
    if (!CollectionUtils.isEmpty(permissions)) {
      this.authorities = permissions.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .toList();
    }
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return userDatas.get("username").toString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
