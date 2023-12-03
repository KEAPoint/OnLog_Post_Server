package keapoint.onlog.post.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostLogoutRes {

    @Schema(name = "isSuccess", example = "true", description = "회원가입 성공 여부")
    boolean isSuccess;
}
