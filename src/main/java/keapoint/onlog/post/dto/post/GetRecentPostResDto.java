package keapoint.onlog.post.dto.post;

import keapoint.onlog.post.dto.blog.BlogDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetRecentPostResDto {
    private UUID postId; // 게시글 식별자
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Boolean modified; // 게시글 수정 여부
    private String category; // 게시글 카테고리
    private BlogDto blog; // 사용자 블로그
}
