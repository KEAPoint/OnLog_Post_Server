package keapoint.onlog.post.dto.blog;

import keapoint.onlog.post.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateBlogReqDto {
    private UUID blogId; // 사용자 블로그 식별자
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogProfileImg; // 사용자 블로그 프로필

    public Blog toEntity(UUID blogId) {
        return Blog.builder()
                .blogId(blogId)
                .blogName(blogName)
                .blogNickname(blogNickname)
                .blogProfileImg(blogProfileImg)
                .blogIntro(blogIntro)
                .blogThemeImg(blogProfileImg)
                .postList(new ArrayList<>())
                .categories(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }
}