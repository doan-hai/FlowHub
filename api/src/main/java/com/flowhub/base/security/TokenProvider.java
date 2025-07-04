package com.flowhub.base.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.flowhub.base.exception.BaseException;
import com.flowhub.base.exception.CommonErrorDef;
import com.flowhub.base.utils.Snowflake;

/**
 * **Lớp `TokenProvider` - Quản lý mã thông báo JWT sử dụng thuật toán RSA**
 *
 * <p>Lớp này chịu trách nhiệm tạo, xác thực và giải mã JWT sử dụng khóa riêng (`PrivateKey`) và
 * khóa công khai (`PublicKey`).</p>
 * <p>
 * **📌 Sử dụng:**
 * <pre>
 * {@code
 * String token = tokenProvider.issuerToken(new Date(System.currentTimeMillis() + 3600000), claims);
 * Claims claims = tokenProvider.getClaimsFromRSAToken(token);
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@Component
public class TokenProvider {

  private static final AtomicReference<PrivateKey> PRIVATE_KEY_CACHE = new AtomicReference<>();

  private static final AtomicReference<PublicKey> PUBLIC_KEY_CACHE = new AtomicReference<>();

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Value("${custom.properties.rsa.private.key:}")
  private String privateKey;

  @Value("${custom.properties.rsa.public.key:}")
  private String publicKey;

  /**
   * **Phát hành mã JWT với ID tự động**
   *
   * <p>Hàm này sử dụng Snowflake để tạo ID duy nhất cho token.</p>
   *
   * @param expiration Ngày hết hạn của token.
   * @param data       Payload (dữ liệu) cần nhúng vào JWT.
   * @return Chuỗi JWT đã ký.
   */
  public String issuerToken(Date expiration, Map<String, Object> data) {
    return issuerToken(String.valueOf(Snowflake.getInstance().nextId()), expiration, data);
  }

  /**
   * **Phát hành mã JWT với ID cụ thể**
   *
   * <p>Hàm này tạo JWT sử dụng thuật toán ký **RS256**.</p>
   *
   * @param id         ID duy nhất của token.
   * @param expiration Ngày hết hạn của token.
   * @param data       Payload (dữ liệu) cần nhúng vào JWT.
   * @return Chuỗi JWT đã ký.
   */
  public String issuerToken(String id, Date expiration, Map<String, Object> data) {
    return Jwts.builder()
               .claims(Jwts.claims().add(data).build())
               .expiration(expiration)
               .id(id)
               .issuedAt(new Date())
               .signWith(getPrivateKey(), Jwts.SIG.RS256)
               .compact();
  }

  /**
   * **Giải mã và xác thực JWT bằng khóa công khai RSA**
   *
   * <p>Hàm này kiểm tra chữ ký số của JWT và trích xuất thông tin payload.</p>
   *
   * @param token Chuỗi JWT cần xác thực.
   * @return Đối tượng `Claims` chứa dữ liệu từ JWT.
   * @throws BaseException Nếu token không hợp lệ hoặc đã hết hạn.
   */
  public Claims getClaimsFromRSAToken(String token) {
    try {
      return Jwts.parser()
                 .verifyWith(getPublicKey())  // Dùng PrivateKey từ phương thức getPrivateKey()
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
    } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException |
             SignatureException ex) {
      log.error("Token invalid", ex);
      throw new BaseException(CommonErrorDef.ACCESS_TOKEN_INVALID);
    } catch (ExpiredJwtException e) {
      log.error("Token expired", e);
      throw new BaseException(CommonErrorDef.ACCESS_TOKEN_EXPIRED);
    }
  }

  /**
   * **Lấy khóa riêng tư RSA từ cấu hình**
   *
   * <p>Hàm này giải mã chuỗi **Base64** của khóa riêng tư và lưu trữ vào cache để tối ưu hiệu
   * suất.</p>
   *
   * @return Đối tượng `PrivateKey` để ký JWT.
   * @throws BaseException Nếu khóa không hợp lệ hoặc không tồn tại.
   */
  private PrivateKey getPrivateKey() throws BaseException {
    if (PRIVATE_KEY_CACHE.get() != null) {
      return PRIVATE_KEY_CACHE.get();
    }
    if (privateKey == null || privateKey.isBlank()) {
      throw new BaseException(CommonErrorDef.INTERNAL_SERVER_ERROR,
                              "Private key is missing or empty");
    }
    try {
      var encoded = Base64.getDecoder().decode(privateKey.replace("-----BEGIN PRIVATE KEY-----",
                                                                  StringUtils.EMPTY)
                                                         .replace("-----END PRIVATE KEY-----",
                                                                  StringUtils.EMPTY)
                                                         .replaceAll("\\s+", StringUtils.EMPTY)
                                                         .trim());
      var keyFactory = KeyFactory.getInstance("RSA");
      var keySpec = new PKCS8EncodedKeySpec(encoded);
      var key = keyFactory.generatePrivate(keySpec);

      PRIVATE_KEY_CACHE.set(key); // Lưu vào cache
      return key;
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      log.error("Failed to parse private key", e);
      throw new BaseException(CommonErrorDef.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * **Lấy khóa công khai RSA từ cấu hình**
   *
   * <p>Hàm này giải mã chuỗi **Base64** của khóa công khai và lưu trữ vào cache để tối ưu hiệu
   * suất.</p>
   *
   * @return Đối tượng `PublicKey` để xác thực JWT.
   * @throws BaseException Nếu khóa không hợp lệ hoặc không tồn tại.
   */
  private PublicKey getPublicKey() throws BaseException {
    if (PUBLIC_KEY_CACHE.get() != null) {
      return PUBLIC_KEY_CACHE.get();
    }
    if (publicKey == null || publicKey.isBlank()) {
      throw new BaseException(CommonErrorDef.INTERNAL_SERVER_ERROR,
                              "Public key is missing or empty");
    }
    try {
      var encoded = Base64.getDecoder()
                          .decode(publicKey.replace("-----BEGIN PUBLIC KEY-----", StringUtils.EMPTY)
                                           .replace("-----END PUBLIC KEY-----", StringUtils.EMPTY)
                                           .replaceAll("\\s+", StringUtils.EMPTY)
                                           .trim());
      var keyFactory = KeyFactory.getInstance("RSA");
      var keySpec = new X509EncodedKeySpec(encoded);
      var key = keyFactory.generatePublic(keySpec);

      PUBLIC_KEY_CACHE.set(key); // Lưu vào cache
      return key;
    } catch (Exception e) {
      log.error("Failed to parse public key", e);
      throw new BaseException(CommonErrorDef.INTERNAL_SERVER_ERROR, "Invalid public key format");
    }
  }
}
