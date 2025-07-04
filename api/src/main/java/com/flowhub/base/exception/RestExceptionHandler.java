package com.flowhub.base.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import com.fasterxml.jackson.databind.JsonMappingException;
import feign.FeignException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.flowhub.base.data.ResponseData;
import com.flowhub.base.data.ResponseUtils;
import com.flowhub.base.utils.JsonUtils;
import com.flowhub.base.utils.RequestUtils;

import static com.flowhub.base.exception.CommonErrorDef.ARGUMENT_NOT_VALID;
import static com.flowhub.base.exception.CommonErrorDef.ARGUMENT_TYPE_MISMATCH;
import static com.flowhub.base.exception.CommonErrorDef.BAD_REQUEST;
import static com.flowhub.base.exception.CommonErrorDef.DATA_INTEGRITY_VIOLATION;
import static com.flowhub.base.exception.CommonErrorDef.EXECUTE_THIRTY_SERVICE_ERROR;
import static com.flowhub.base.exception.CommonErrorDef.FORBIDDEN;
import static com.flowhub.base.exception.CommonErrorDef.HTTP_MESSAGE_NOT_READABLE;
import static com.flowhub.base.exception.CommonErrorDef.INTERNAL_SERVER_ERROR;
import static com.flowhub.base.exception.CommonErrorDef.MEDIA_TYPE_NOT_ACCEPTABLE;
import static com.flowhub.base.exception.CommonErrorDef.METHOD_NOT_ALLOWED;
import static com.flowhub.base.exception.CommonErrorDef.MISSING_REQUEST_HEADER;
import static com.flowhub.base.exception.CommonErrorDef.MISSING_REQUEST_PARAMETER;
import static com.flowhub.base.exception.CommonErrorDef.MISSING_REQUEST_PATH;
import static com.flowhub.base.exception.CommonErrorDef.NOT_FOUND;
import static com.flowhub.base.exception.CommonErrorDef.TOO_MANY_REQUESTS;
import static com.flowhub.base.exception.CommonErrorDef.UNSUPPORTED_MEDIA_TYPE;

/**
 * **Lớp `RestExceptionHandler` - Xử lý ngoại lệ toàn cục trong API**
 *
 * <p>Lớp này sử dụng `@RestControllerAdvice` để xử lý các ngoại lệ phát sinh
 * trong hệ thống, giúp chuẩn hóa phản hồi lỗi và ghi log lỗi chi tiết.</p>
 * <p>
 * **📌 Cách hoạt động:**
 * <p>Khi xảy ra lỗi trong API, hệ thống sẽ tự động gọi `RestExceptionHandler`,
 * sau đó hệ thống sẽ:</p>
 * <ul>
 *   <li>📌 Xác định loại lỗi (request, server, xác thực).</li>
 *   <li>📌 Chuyển lỗi thành phản hồi JSON chuẩn.</li>
 *   <li>📌 Ghi log chi tiết lỗi.</li>
 *   <li>📌 Trả về mã trạng thái HTTP phù hợp.</li>
 * </ul>
 * <p>
 * **📌 Ví dụ phản hồi JSON khi xảy ra lỗi:**
 * <pre>
 * {
 *     "errorCode": "CM_0004",
 *     "message": "Request không hợp lệ",
 *     "detailedMessage": "Trường 'email' không được để trống",
 *     "data": null
 * }
 * </pre>
 *
 * @author haidv
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

  private final MessageSource messageSource;

  /**
   * **Xử lý lỗi thiếu tham số trong request (`handleMissingServletRequestParameter`)**
   *
   * @param ex Ngoại lệ `MissingServletRequestParameterException`
   * @return Phản hồi lỗi dạng JSON với mã lỗi `MISSING_REQUEST_PARAMETER`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex) {
    log.error("handleMissingServletRequestParameter: {}", ex.getMessage());
    return this.handleError(MISSING_REQUEST_PARAMETER,
                            new Object[]{ex.getParameterType(), ex.getParameterName()},
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi thiếu phần dữ liệu trong request (`handleMissingServletRequestPart`)**
   *
   * <p>Xảy ra khi một `multipart` request yêu cầu một phần dữ liệu (file hoặc form-data)
   * nhưng không được gửi lên.</p>
   *
   * @param ex Ngoại lệ `MissingServletRequestPartException`
   * @return Phản hồi lỗi với mã lỗi `MISSING_REQUEST_PARAMETER`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException ex) {
    log.error("handleMissingServletRequestPart: {}", ex.getMessage());
    return this.handleError(MISSING_REQUEST_PARAMETER,
                            new Object[]{"request part", ex.getRequestPartName()},
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi request có header bị thiếu (`handleServletRequestBindingException`)**
   *
   * @param ex Ngoại lệ `ServletRequestBindingException`
   * @return Phản hồi lỗi với mã lỗi `MISSING_REQUEST_HEADER`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(ServletRequestBindingException.class)
  public ResponseEntity<Object> handleServletRequestBindingException(
      ServletRequestBindingException ex) {
    log.error("handleServletRequestBindingException: {}", ex.getMessage());
    return this.handleError(MISSING_REQUEST_HEADER,
                            new Object[]{((MissingRequestHeaderException) ex).getHeaderName()},
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi loại dữ liệu không được hỗ trợ (`handleHttpMediaTypeNotSupported`)**
   *
   * <p>Xảy ra khi client gửi request với `Content-Type` không được hỗ trợ.</p>
   *
   * @param ex Ngoại lệ `HttpMediaTypeNotSupportedException`
   * @return Phản hồi lỗi với mã lỗi `UNSUPPORTED_MEDIA_TYPE`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex) {
    log.error("handleHttpMediaTypeNotSupported: {}", ex.getMessage());
    return this.handleError(UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
  }

  /**
   * **Xử lý lỗi phương thức HTTP không được hỗ trợ (`handleHttpRequestMethodNotSupported`)**
   *
   * <p>Xảy ra khi client gửi request với phương thức HTTP không hợp lệ cho endpoint.</p>
   *
   * @param ex Ngoại lệ `HttpRequestMethodNotSupportedException`
   * @return Phản hồi lỗi với mã lỗi `METHOD_NOT_ALLOWED`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex) {
    log.error("handleHttpRequestMethodNotSupported: {}", ex.getMessage());
    return this.handleError(METHOD_NOT_ALLOWED, ex.getMessage());
  }

  /**
   * **Xử lý lỗi dữ liệu đầu vào không hợp lệ (`handleMethodArgumentNotValid`)**
   *
   * @param ex Ngoại lệ `MethodArgumentNotValidException`
   * @return Phản hồi lỗi với danh sách lỗi chi tiết.
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    log.error("handleMethodArgumentNotValid: {}", ex.getMessage());
    return this.handleError(ARGUMENT_NOT_VALID, null, this.getSubErrors(ex), ex.getMessage());
  }

  /**
   * **Xử lý lỗi xác thực dữ liệu tham số trong controller
   * (`handleHandlerMethodValidationException`)**
   *
   * <p>Xảy ra khi một phương thức handler nhận được tham số không hợp lệ do validation.</p>
   *
   * @param ex Ngoại lệ `HandlerMethodValidationException`
   * @return Phản hồi lỗi với mã lỗi `ARGUMENT_NOT_VALID`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HandlerMethodValidationException.class)
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.error("handleHandlerMethodValidationException: {}", ex.getMessage());
    return this.handleError(ARGUMENT_NOT_VALID, null, this.getSubErrors(ex), ex.getMessage());
  }

  /**
   * **Xử lý lỗi quá nhiều yêu cầu (`handleRequestNotPermittedException`)**
   *
   * <p>Xảy ra khi người dùng gửi quá nhiều request trong một khoảng thời gian ngắn,
   * vi phạm giới hạn tần suất của hệ thống.</p>
   *
   * @param ex Ngoại lệ `RequestNotPermitted`
   * @return Phản hồi lỗi với mã lỗi `TOO_MANY_REQUESTS`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(RequestNotPermitted.class)
  protected ResponseEntity<Object> handleRequestNotPermittedException(RequestNotPermitted ex) {
    log.error("handleRequestNotPermittedException: {}", ex.getMessage());
    return this.handleError(TOO_MANY_REQUESTS, null, null, ex.getMessage());
  }

  /**
   * **Xử lý lỗi không thể đọc dữ liệu từ request (`handleHttpMessageNotReadable`)**
   *
   * <p>Xảy ra khi request body có định dạng không hợp lệ hoặc không thể parse được thành
   * object.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Ghi log lỗi để theo dõi nguyên nhân.</li>
   *   <li>📌 Nếu nguyên nhân lỗi là `JsonMappingException`, kiểm tra xem có phải do định dạng ngày giờ sai.</li>
   *   <li>📌 Nếu có `DateTimeFormatException`, tạo danh sách lỗi chi tiết với thông tin trường và giá trị lỗi.</li>
   *   <li>📌 Nếu không phải lỗi về ngày giờ, trả về phản hồi lỗi chuẩn `HTTP_MESSAGE_NOT_READABLE`.</li>
   * </ul>
   * <p>
   * **📌 Ví dụ tình huống lỗi:**
   * - Người dùng gửi request có `createdDate: "31-02-2023"` (không hợp lệ).
   * - Hệ thống sẽ ném `DateTimeFormatException` và phản hồi lỗi như sau:
   *
   * <pre>
   * {
   *     "errorCode": "ARGUMENT_NOT_VALID",
   *     "message": "Invalid date format",
   *     "detailedMessage": "Trường 'createdDate' có giá trị '31-02-2023' không hợp lệ",
   *     "data": null
   * }
   * </pre>
   *
   * @param ex Ngoại lệ `HttpMessageNotReadableException`
   * @return Phản hồi lỗi với mã lỗi `HTTP_MESSAGE_NOT_READABLE`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    log.error("handleHttpMessageNotReadable: {}", ex.getMessage());
    if (ex.getCause() instanceof JsonMappingException jsonMappingException
        && jsonMappingException.getCause() instanceof DateTimeFormatException dateTimeFormatException) {
      return this.handleError(ARGUMENT_NOT_VALID,
                              null,
                              List.of(new SubError(dateTimeFormatException.getField(),
                                                   dateTimeFormatException.getValue(),
                                                   dateTimeFormatException.getMessage())),
                              ex.getMessage());
    }
    return this.handleError(HTTP_MESSAGE_NOT_READABLE, ex.getMessage());
  }

  @ExceptionHandler(DateTimeFormatException.class)
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      DateTimeFormatException ex) {
    log.error("handleHttpMessageNotReadable: {}", ex.getMessage());
    return this.handleError(ARGUMENT_NOT_VALID,
                            null,
                            List.of(new SubError(ex.getField(),
                                                 ex.getValue(),
                                                 ex.getMessage())),
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi không thể ghi dữ liệu ra response (`handleHttpMessageNotWritable`)**
   *
   * <p>Xảy ra khi hệ thống gặp lỗi trong quá trình serialize dữ liệu response.</p>
   *
   * @param ex Ngoại lệ `HttpMessageNotWritableException`
   * @return Phản hồi lỗi với mã lỗi `BAD_REQUEST`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpMessageNotWritableException.class)
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex) {
    log.error("handleHttpMessageNotWritable: {}", ex.getMessage());
    return this.handleError(BAD_REQUEST, ex.getMessage());
  }

  /**
   * **Xử lý lỗi không tìm thấy endpoint (`handleNoHandlerFoundException`)**
   *
   * <p>Xảy ra khi client gửi request đến một endpoint không tồn tại.</p>
   *
   * @param ex Ngoại lệ `NoHandlerFoundException`
   * @return Phản hồi lỗi với mã lỗi `NOT_FOUND`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(NoHandlerFoundException.class)
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
    log.error("handleNoHandlerFoundException: {}", ex.getMessage());
    return this.handleError(NOT_FOUND,
                            new Object[]{ex.getHttpMethod(), ex.getRequestURL()},
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi kiểu dữ liệu không thể chấp nhận (`handleHttpMediaTypeNotAcceptable`)**
   *
   * <p>Xảy ra khi client yêu cầu một `Accept` header không phù hợp với server.</p>
   *
   * @param ex Ngoại lệ `HttpMediaTypeNotAcceptableException`
   * @return Phản hồi lỗi với mã lỗi `MEDIA_TYPE_NOT_ACCEPTABLE`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex) {
    log.error("handleHttpMediaTypeNotAcceptable: {}", ex.getMessage());
    return this.handleError(MEDIA_TYPE_NOT_ACCEPTABLE, ex.getMessage());
  }

  /**
   * **Xử lý lỗi vi phạm tính toàn vẹn dữ liệu (`handleDataIntegrityViolation`)**
   *
   * <p>Xảy ra khi có xung đột dữ liệu, chẳng hạn như ràng buộc duy nhất hoặc khóa ngoại.</p>
   *
   * @param ex Ngoại lệ `DataIntegrityViolationException`
   * @return Phản hồi lỗi với mã lỗi `DATA_INTEGRITY_VIOLATION`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    log.error("handleDataIntegrityViolation: {}", ex.getMessage(), ex);
    return this.handleError(DATA_INTEGRITY_VIOLATION, ex.getMessage());
  }

  /**
   * **Xử lý lỗi quyền truy cập (`handleAccessDeniedException`)**
   *
   * @param ex Ngoại lệ `AccessDeniedException`
   * @return Phản hồi lỗi với mã lỗi `FORBIDDEN`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(AccessDeniedException.class)
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
    log.error("handleAccessDeniedException: {}", ex.getMessage());
    return this.handleError(FORBIDDEN, ex.getMessage());
  }

  /**
   * **Xử lý lỗi sai kiểu dữ liệu của tham số (`handleMethodArgumentTypeMismatch`)**
   *
   * <p>Xảy ra khi một tham số của request không đúng kiểu dữ liệu mong đợi.</p>
   *
   * @param ex Ngoại lệ `MethodArgumentTypeMismatchException`
   * @return Phản hồi lỗi với mã lỗi `ARGUMENT_TYPE_MISMATCH`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    log.error("handleMethodArgumentTypeMismatch: {}", ex.getMessage());
    return this.handleError(ARGUMENT_TYPE_MISMATCH,
                            new Object[]{ex.getRequiredType(), ex.getName()},
                            ex.getMessage());
  }

  /**
   * **Xử lý ngoại lệ chung của hệ thống (`handleBaseException`)**
   *
   * <p>Xảy ra khi hệ thống ném ra một ngoại lệ `BaseException` do lỗi nghiệp vụ.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Lấy mã lỗi (`errorCode`) và trạng thái HTTP (`httpStatus`) từ `BaseException`.</li>
   *   <li>📌 Sử dụng `MessageSource` để lấy thông điệp lỗi phù hợp với ngôn ngữ của người dùng.</li>
   *   <li>📌 Ghi log chi tiết lỗi và trả về phản hồi chuẩn.</li>
   * </ul>
   *
   * @param ex Ngoại lệ `BaseException`
   * @return Phản hồi lỗi với mã lỗi cụ thể từ `BaseException`
   */
  @ExceptionHandler(BaseException.class)
  protected ResponseEntity<ResponseData<Object>> handleBaseException(BaseException ex) {
    log.error("handleBaseException: {}", ex.getLocalizedMessage(), ex);
    List<SubError> subErrors = null;
    var errorCode = ex.getErrorCode();
    HttpStatus httpStatus = ex.getHttpStatus();
    return ResponseUtils.error(errorCode,
                               ex.getMessage(),
                               messageSource.getMessage(errorCode,
                                                        ex.getMessageArg(),
                                                        ex.getMessage(),
                                                        RequestUtils.extractLocale()),
                               subErrors,
                               httpStatus);
  }

  /**
   * **Xử lý lỗi khi gọi API bên thứ ba (`handleHttpClientErrorException`)**
   *
   * <p>Xảy ra khi hệ thống gửi request đến một dịch vụ bên ngoài và nhận phản hồi lỗi.</p>
   *
   * @param ex Ngoại lệ `HttpClientErrorException`
   * @return Phản hồi lỗi với mã lỗi `EXECUTE_THIRTY_SERVICE_ERROR`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(HttpClientErrorException.class)
  protected ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex) {
    log.error("handleHttpClientErrorException: {}", ex.getLocalizedMessage(), ex);
    return this.handleError(EXECUTE_THIRTY_SERVICE_ERROR, ex.getMessage());
  }

  /**
   * **Xử lý lỗi vi phạm ràng buộc dữ liệu (`handleConstraintViolationException`)**
   *
   * <p>Xảy ra khi một giá trị nhập vào không tuân theo ràng buộc validation của Hibernate
   * Validator.</p>
   *
   * @param ex Ngoại lệ `ConstraintViolationException`
   * @return Phản hồi lỗi với mã lỗi `ARGUMENT_NOT_VALID`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.error("handleConstraintViolationException: {}", ex.getLocalizedMessage(), ex);
    return this.handleError(ARGUMENT_NOT_VALID, null, getSubErrors(ex), ex.getMessage());
  }

  /**
   * **Xử lý lỗi khi gọi API bên thứ ba bằng Feign Client (`handleFeignException`)**
   *
   * <p>Xảy ra khi hệ thống gọi một service bên ngoài qua Feign Client và nhận lỗi.</p>
   *
   * @param ex Ngoại lệ `FeignException`
   * @return Phản hồi lỗi dựa trên nội dung phản hồi từ service bên ngoài hoặc lỗi chung.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @ExceptionHandler(FeignException.class)
  protected ResponseEntity<ResponseData<Object>> handleFeignException(FeignException ex) {
    log.error("handleFeignException: {}", ex.getLocalizedMessage(), ex);
    if (!StringUtils.isEmpty(ex.contentUTF8())) {
      log.error("FeignException content: {}", ex.contentUTF8());
      ResponseData responseData = JsonUtils.fromJson(ex.contentUTF8(), ResponseData.class);
      if (responseData != null) {
        return ResponseUtils.error(responseData.getCode(),
                                   responseData.getMessage(),
                                   null,
                                   HttpStatus.resolve(ex.status()));
      }
    }
    return this.handleError(INTERNAL_SERVER_ERROR, null, null);
  }

  /**
   * **Xử lý lỗi thiếu biến đường dẫn (`handleMissingPathVariable`)**
   *
   * <p>Xảy ra khi một biến trong `@PathVariable` bị thiếu trong URL request.</p>
   *
   * @param ex Ngoại lệ `MissingPathVariableException`
   * @return Phản hồi lỗi với mã lỗi `MISSING_REQUEST_PATH`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(MissingPathVariableException.class)
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex) {
    log.error("handleMissingPathVariable: {}", ex.getLocalizedMessage());
    return this.handleError(MISSING_REQUEST_PATH,
                            new Object[]{ex.getVariableName()},
                            ex.getMessage());
  }

  /**
   * **Xử lý lỗi hệ thống chung (`handleAllException`)**
   *
   * @param ex Ngoại lệ chung
   * @return Phản hồi lỗi với mã lỗi `INTERNAL_SERVER_ERROR`
   */
  @SuppressWarnings("unchecked")
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleAllException(Exception ex) {
    log.error("handleAllException: {}", ex.getLocalizedMessage(), ex);
    return this.handleError(INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  /**
   * **Tạo phản hồi lỗi chuẩn (`handleError`)**
   *
   * <p>Phương thức này giúp chuẩn hóa phản hồi lỗi bằng cách lấy mã lỗi từ `CommonErrorDef`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Gọi `handleError(CommonErrorDef, Object[], String)` để xử lý lỗi.</li>
   *   <li>📌 Nếu có `messageArg`, hệ thống sử dụng để format thông điệp lỗi.</li>
   * </ul>
   *
   * @param commonError Mã lỗi chung từ `CommonErrorDef`
   * @param errorDesc   Mô tả lỗi
   * @return `ResponseEntity` chứa thông tin lỗi.
   */
  @SuppressWarnings("rawtypes")
  private ResponseEntity handleError(CommonErrorDef commonError, String errorDesc) {
    return this.handleError(commonError, null, errorDesc);
  }

  /**
   * **Tạo phản hồi lỗi có tham số (`handleError`)**
   *
   * <p>Phương thức này hỗ trợ xử lý lỗi với tham số động trong thông báo lỗi.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Nếu `messageArg` không null, hệ thống sẽ format `errorDesc`.</li>
   *   <li>📌 Gọi phương thức chính `handleError(CommonErrorDef, Object[], List<SubError>, String)`.</li>
   * </ul>
   *
   * @param commonError Mã lỗi chung
   * @param messageArg  Tham số để format thông báo lỗi
   * @param errorDesc   Mô tả lỗi
   * @return `ResponseEntity` chứa thông tin lỗi.
   */
  @SuppressWarnings("rawtypes")
  private ResponseEntity handleError(CommonErrorDef commonError, Object[] messageArg,
                                     String errorDesc) {
    return this.handleError(commonError, messageArg, null, errorDesc);
  }

  /**
   * **Tạo phản hồi lỗi chi tiết (`handleError`)**
   *
   * <p>Phương thức chính để xử lý lỗi trong hệ thống, hỗ trợ danh sách lỗi chi tiết.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Lấy mã lỗi (`errorCode`) từ `CommonErrorDef`.</li>
   *   <li>📌 Format `errorDesc` nếu có `messageArg`.</li>
   *   <li>📌 Lấy thông điệp lỗi theo ngôn ngữ người dùng từ `MessageSource`.</li>
   *   <li>📌 Trả về phản hồi lỗi có đầy đủ thông tin lỗi chi tiết.</li>
   * </ul>
   *
   * @param commonError Mã lỗi chung từ `CommonErrorDef`
   * @param messageArg  Tham số để format thông báo lỗi
   * @param subErrors   Danh sách lỗi chi tiết
   * @param errorDesc   Mô tả lỗi
   * @return `ResponseEntity` chứa thông tin lỗi.
   */
  @SuppressWarnings("rawtypes")
  private ResponseEntity handleError(CommonErrorDef commonError, Object[] messageArg,
                                     List<SubError> subErrors, String errorDesc) {
    var errorCode = commonError.getErrorCode();
    if (messageArg != null) {
      errorDesc = String.format(errorDesc, messageArg);
    }
    HttpStatus httpStatus = commonError.getHttpStatus();
    return ResponseUtils.error(errorCode,
                               errorDesc,
                               messageSource.getMessage(errorCode,
                                                        messageArg,
                                                        errorDesc,
                                                        RequestUtils.extractLocale()),
                               subErrors,
                               httpStatus);
  }

  /**
   * **Lấy danh sách lỗi từ `MethodArgumentNotValidException`**
   *
   * <p>Chuyển đổi danh sách lỗi từ `BindingResult` thành danh sách `SubError`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả lỗi trong `FieldError`.</li>
   *   <li>📌 Tạo một `SubError` mới cho mỗi lỗi.</li>
   *   <li>📌 Trả về danh sách `SubError`.</li>
   * </ul>
   *
   * @param e Ngoại lệ `MethodArgumentNotValidException`
   * @return Danh sách `SubError` chứa thông tin lỗi chi tiết.
   */
  private List<SubError> getSubErrors(MethodArgumentNotValidException e) {
    List<SubError> subErrors = new ArrayList<>();
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      SubError subError = new SubError(fieldError.getField(),
                                       fieldError.getRejectedValue(),
                                       fieldError.getDefaultMessage());
      subErrors.add(subError);
    }
    return subErrors;
  }

  /**
   * **Lấy danh sách lỗi từ `ConstraintViolationException`**
   *
   * <p>Chuyển đổi danh sách lỗi từ `ConstraintViolation` thành danh sách `SubError`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả `ConstraintViolation`.</li>
   *   <li>📌 Tạo `SubError` từ `propertyPath`, `invalidValue` và `message`.</li>
   *   <li>📌 Trả về danh sách `SubError`.</li>
   * </ul>
   *
   * @param e Ngoại lệ `ConstraintViolationException`
   * @return Danh sách `SubError` chứa thông tin lỗi chi tiết.
   */
  @SuppressWarnings("rawtypes")
  private List<SubError> getSubErrors(ConstraintViolationException e) {
    List<SubError> subErrors = new ArrayList<>();
    for (ConstraintViolation violation : e.getConstraintViolations()) {
      subErrors.add(new SubError(violation.getPropertyPath().toString(),
                                 violation.getInvalidValue(),
                                 violation.getMessage()));
    }
    return subErrors;
  }

  /**
   * **Lấy danh sách lỗi từ `HandlerMethodValidationException`**
   *
   * <p>Chuyển đổi danh sách lỗi từ `HandlerMethodValidationException` thành `SubError`.</p>
   * <p>
   * **📌 Cách hoạt động:**
   * <ul>
   *   <li>📌 Duyệt qua tất cả lỗi trong danh sách `getAllErrors()`.</li>
   *   <li>📌 Trích xuất thông tin lỗi và tạo `SubError` tương ứng.</li>
   *   <li>📌 Trả về danh sách `SubError`.</li>
   * </ul>
   *
   * @param e Ngoại lệ `HandlerMethodValidationException`
   * @return Danh sách `SubError` chứa thông tin lỗi chi tiết.
   */
  @SuppressWarnings("unchecked")
  private List<SubError> getSubErrors(HandlerMethodValidationException e) {
    List<SubError> subErrors = new ArrayList<>();
    List<DefaultMessageSourceResolvable> fieldErrors = (List<DefaultMessageSourceResolvable>) e.getAllErrors();
    for (DefaultMessageSourceResolvable fieldError : fieldErrors) {
      SubError subError = new SubError(((DefaultMessageSourceResolvable) fieldError.getArguments()[0]).getDefaultMessage(),
                                       null,
                                       fieldError.getDefaultMessage());
      subErrors.add(subError);
    }
    return subErrors;
  }

  /**
   * @author haidv
   * @version 1.0
   */
  @Getter
  @RequiredArgsConstructor
  @Schema(description = "Chi tiết lỗi validate")
  public static class SubError {

    @Schema(description = "Tên trường lỗi")
    private final String fieldName;

    @Schema(description = "Giá trị lỗi")
    private final Object fieldValue;

    @Schema(description = "Thông báo lỗi")
    private final String message;
  }
}
