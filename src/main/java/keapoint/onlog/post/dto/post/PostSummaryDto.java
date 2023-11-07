package keapoint.onlog.post.dto.post;

import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.topic.TopicDto;
import keapoint.onlog.post.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDto {
    private UUID postId; // 게시글 식별자
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Long likesCount; // 게시글 좋아요 갯수
    private CategoryDto category; // 게시글 카테고리
    private TopicDto topic; // 게시글 주제
    private int commentsCounts; // 게시글 댓글 갯수
    private BlogDto writer; // 작성자
    private LocalDateTime createdAt; // 게시글 작성 시간

    public PostSummaryDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.thumbnailLink = post.getThumbnailLink();
        this.likesCount = post.getLikesCount();
        this.category = new CategoryDto(post.getCategory());
        this.topic = new TopicDto(post.getTopic());
        this.commentsCounts = post.getComments().size();
        this.writer = new BlogDto(post.getWriter());
        this.createdAt = post.getCreatedAt();
    }
}
