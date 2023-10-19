package keapoint.onlog.post.dto.post;

import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.entity.Post;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetRecentPostListResDto {
    private UUID postId; // 게시글 식별자
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Boolean modified; // 게시글 수정 여부
    private String category; // 게시글 카테고리
    private BlogDto blog; // 사용자 블로그

    public static GetRecentPostListResDto fromPost(Post post, BlogDto blog) {
        return GetRecentPostListResDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .thumbnailLink(post.getThumbnailLink())
                .modified(post.getModified())
                .category(post.getCategory().getName())
                .blog(blog)
                .build();
    }

    public static GetRecentPostListResDto fromPost(Post post) {
        return GetRecentPostListResDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .thumbnailLink(post.getThumbnailLink())
                .modified(post.getModified())
                .category(post.getCategory().getName())
                .blog(BlogDto.fromBlog(post.getWriter()))
                .build();
    }
}
