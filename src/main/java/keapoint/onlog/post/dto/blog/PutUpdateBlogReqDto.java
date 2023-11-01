package keapoint.onlog.post.dto.blog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutUpdateBlogReqDto {
    private String blogName; // 사용자 블로그 이름
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogProfileImg; // 사용자 블로그 프로필
}
