package keapoint.onlog.post.dto.blog;

import keapoint.onlog.post.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class BlogDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필

    public BlogDto fromPost(Post post) {
        BlogDto dto = new BlogDto();

        dto.blogId = post.getBlog().getBlogId();
        dto.blogName = post.getBlog().getBlogName();
        dto.blogNickname = post.getBlog().getBlogNickname();
        dto.blogProfileImg = post.getBlog().getBlogProfileImg();

        return dto;
    }
}
