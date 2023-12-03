package keapoint.onlog.post.config;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 의도된 Exception
    @ExceptionHandler(BaseException.class)
    public BaseResponse<BaseErrorCode> handleBaseException(BaseException exception) {
        log.warn("BaseException. error message: {}", exception.getErrorCode().getMessage());
        exception.printStackTrace();
        return new BaseResponse<>(exception);
    }

    // 의도되지 않은 Exception
    @ExceptionHandler(Exception.class)
    public BaseResponse<BaseErrorCode> handleException(Exception exception) {
        log.error("Exception has raised: {}", exception.getMessage());
        return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
    }
}