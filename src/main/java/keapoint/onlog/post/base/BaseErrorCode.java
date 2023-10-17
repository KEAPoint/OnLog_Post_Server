package keapoint.onlog.post.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    /**
     * 404 Not Found
     */
    POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 게시글입니다."),

    /**
     * 409 Conflict
     */
    ALREADY_BLOG_EXISTS_EXCEPTION(HttpStatus.CONFLICT.value(), "해당 ID를 가진 블로그가 이미 존재합니다."),

    /**
     * 500 : INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "처리 중에 오류가 발생하였습니다."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생하였습니다.");

    private final Integer status;
    private final String message;
}
