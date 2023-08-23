package umc.stockoneqback.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.global.exception.dto.request.DiscordMessage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiGlobalExceptionHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${discord.webhookId}")
    private String DISCORD_WEBHOOK_ID;
    @Value("${discord.webhookToken}")
    private String DISCORD_WEBHOOK_TOKEN;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> applicationException(BaseException e, HttpServletRequest request) {
        ErrorCode code = e.getCode();
        logging(code);
        sendDiscordAlertErrorLog(e.getCode(), request);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    /**
     * 요청 데이터 Validation 전용 ExceptionHandler (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        return convert(GlobalErrorCode.VALIDATION_ERROR, extractErrorMessage(fieldErrors));
    }

    private String extractErrorMessage(List<FieldError> fieldErrors) {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        StringBuffer buffer = new StringBuffer();
        for (FieldError error : fieldErrors) {
            buffer.append(error.getDefaultMessage()).append("\n");
        }
        return buffer.toString();
    }

    /**
     * 존재하지 않는 Endpoint 전용 ExceptionHandler
     */
    @ExceptionHandler({NoHandlerFoundException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> noHandlerFoundException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_URI_ERROR);
    }

    /**
     * HTTP Request Method 오류 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_METHOD_ERROR);
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException() {
        return convert(GlobalErrorCode.NOT_SUPPORTED_MEDIA_TYPE_ERROR);
    }

    /**
     * 내부 서버 오류 전용 ExceptionHandler (With Discord Alert)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnyException(RuntimeException e, HttpServletRequest request) {
        sendDiscordAlertErrorLog(GlobalErrorCode.INTERNAL_SERVER_ERROR, request);
        return convert(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Exception (With Discord Alert)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnonymousException(Exception e, HttpServletRequest request) {
        ErrorCode code = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        logging(code, e.getMessage());

        sendDiscordAlertErrorLog(code, request);

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.from(code));
    }

    private ResponseEntity<ErrorResponse> convert(ErrorCode code, String message) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.of(code, message));
    }

    private void logging(ErrorCode code) {
        log.warn("{} | {} | {}", code.getStatus(), code.getErrorCode(), code.getMessage());
    }

    private void logging(ErrorCode code, String message) {
        log.warn("{} | {} | {}", code.getStatus(), code.getErrorCode(), message);
    }

    private void sendDiscordAlertErrorLog(ErrorCode code, HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; utf-8");

        HttpEntity<DiscordMessage> messageEntity = new HttpEntity<>(getDiscordMessage(code, request), httpHeaders);

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.exchange(
                "https://discord.com/api/webhooks/" + DISCORD_WEBHOOK_ID + "/" + DISCORD_WEBHOOK_TOKEN,
                HttpMethod.POST,
                messageEntity,
                String.class
        );

        if(response.getStatusCode().value() != HttpStatus.NO_CONTENT.value()) {
            log.error("메시지 전송 이후 에러 발생");
        }
    }

    private DiscordMessage getDiscordMessage(ErrorCode code, HttpServletRequest request) {
        String content = "## ❗️ 서버 API 에러 알림\n\n" +
                "### 에러 정보\n" +
                "```\n" +
                "- 🕖 Occurrence Time: " + LocalDateTime.now() + "\n" +
                "- 🔗 Endpoint: " + createRequestFullPath(request) + "\n" +
                "- 📌 Status: " + code.getStatus() + "\n" +
                "- 🆖 ErrorCode: " + code.getErrorCode() + "\n" +
                "- 💌 Message: " + code.getMessage() + "\n" +
                "- 🙋 Logged User Id: " + extractUserIdFromRequest(request).toString() + "\n" +
                "```\n" +
                "> 이슈를 해결하시려면 위의 정보와 [StockOneQ 사이트](http://stockoneq.store)을 참고해주시기 바랍니다.";

        return new DiscordMessage(content);
    }

    private String createRequestFullPath(HttpServletRequest request) {
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtTokenProvider.isTokenValid(token)) {
                return jwtTokenProvider.getId(token);
            }
        }

        return null;
    }
}
