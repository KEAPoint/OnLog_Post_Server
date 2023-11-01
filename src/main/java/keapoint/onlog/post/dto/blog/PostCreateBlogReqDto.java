package keapoint.onlog.post.dto.blog;

import keapoint.onlog.post.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostCreateBlogReqDto {
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogProfileImg; // 사용자 블로그 프로필

    public Blog toEntity(UUID blogId) {
        return Blog.builder()
                .blogId(blogId)
                .blogName(blogName)
                .blogNickname(blogNickname)
                .blogIntro(blogIntro)
                .blogProfileImg(blogProfileImg)
                .build();
    }
}
