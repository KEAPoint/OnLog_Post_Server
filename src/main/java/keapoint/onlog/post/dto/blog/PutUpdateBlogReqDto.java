package keapoint.onlog.post.dto.blog;

import lombok.Data;

@Data
public class PutUpdateBlogReqDto {
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogProfileImg; // 사용자 블로그 프로필
}
