package keapoint.onlog.post.dto.blog;

import keapoint.onlog.post.entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogThemeImg; // 사용자 블로그 테마 이미지

    public BlogDto(Blog blog) {
        this.blogId = blog.getBlogId();
        this.blogName = blog.getBlogName();
        this.blogNickname = blog.getBlogNickname();
        this.blogProfileImg = blog.getBlogProfileImg();
        this.blogIntro = blog.getBlogIntro();
        this.blogThemeImg = blog.getBlogThemeImg();
    }
}
