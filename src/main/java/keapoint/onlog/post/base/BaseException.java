package keapoint.onlog.post.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends Exception {
    BaseErrorCode errorCode;
}
