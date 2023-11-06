package keapoint.onlog.post.dto.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PutUpdateBlogReqDto {
    @Schema(description = "사용자 블로그 이름", type = "string", example = "승현이의 온로그")
    private String blogName;

    @Schema(description = "사용자 블로그 별명", type = "string", example = "망붕이")
    private String blogNickname;

    @Schema(description = "사용자 블로그 한 줄 소개", type = "string", example = "승현이의 블로그에 오신 것을 환영합니다")
    private String blogIntro;

    @Schema(description = "사용자 블로그 프로필", type = "string", example = "https://upload3.inven.co.kr/upload/2022/01/15/bbs/i14738178643.png")
    private String blogProfileImg;
}
