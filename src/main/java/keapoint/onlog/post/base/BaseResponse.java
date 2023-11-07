package keapoint.onlog.post.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "data"})
public class BaseResponse<T> {

    private boolean isSuccess;
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 일반적인 요청 성공시
     */
    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>(true, HttpStatus.OK.value(), "요청이 성공적으로 처리되었습니다.", result);
    }

    /**
     * 리소스 생성 요청 성공시 (POST, PUT)
     */
    public static <T> BaseResponse<T> onCreate(T result) {
        return new BaseResponse<>(true, HttpStatus.CREATED.value(), "요청이 성공적으로 처리되어 새로운 리소스가 생성되었습니다.", result);
    }

    public BaseResponse(BaseException exception) {
        this.isSuccess = false;
        this.code = exception.errorCode.getStatus();
        this.message = exception.errorCode.getMessage();
    }
}
