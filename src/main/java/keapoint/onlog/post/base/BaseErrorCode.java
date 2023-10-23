package keapoint.onlog.post.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode {

    /**
     * 400 Bad Request
     */
    EXPECTED_LIKE_STATE_EXCEPTION(HttpStatus.BAD_REQUEST.value(), "요청한 '좋아요' 상태와 현재 '좋아요' 상태가 일치하지 않습니다."),
    INVALID_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 토큰입니다."),

    /**
     * 401 Unauthorized
     */
    TOKEN_DECODING_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다."),

    /**
     * 404 Not Found
     */
    BLOG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 블로그입니다."),
    POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 게시글입니다."),
    TOPIC_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 주제입니다."),
    HASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 해시태그입니다."),
    COMMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND.value(), "존재하지 않는 댓글입니다."),

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
