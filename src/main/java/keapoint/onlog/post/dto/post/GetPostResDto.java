package keapoint.onlog.post.dto.post;

import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.entity.Comment;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetPostResDto {
    private UUID postId; // 게시글 식별자
    private Long postHits; // 게시글 방문 횟수
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Boolean modified; // 게시글 수정 여부
    private String category; // 게시글 카테고리
    private List<String> hashtags; // 게시글 hashtag
    private List<Comment> comments; // 게시글 댓글
    private BlogDto blog; // 사용자 블로그
}
