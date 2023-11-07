package keapoint.onlog.post.dto.blog;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogProfileDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필
    private String blogIntro; // 사용자 블로그 한 줄 소개
    private String blogThemeImg; // 사용자 블로그 테마 이미지
    private long postCount; // 작성한 글 개수
    private long likeCount; // 좋아요 개수
    private long subscriberCount; // 구독자 수

    public BlogProfileDto(Blog blog, long subscriberCount) {
        this.blogId = blog.getBlogId();
        this.blogName = blog.getBlogName();
        this.blogNickname = blog.getBlogNickname();
        this.blogProfileImg = blog.getBlogProfileImg();
        this.blogIntro = blog.getBlogIntro();
        this.blogThemeImg = blog.getBlogThemeImg();
        this.postCount = blog.getPostList().size();
        this.likeCount = blog.getPostList().stream()
                .mapToLong(Post::getLikesCount)
                .sum();
        this.subscriberCount = subscriberCount;
    }
}
