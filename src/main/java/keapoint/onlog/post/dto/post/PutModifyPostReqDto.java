package keapoint.onlog.post.dto.post;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PutModifyPostReqDto {
    private UUID postId; // 게시글 식별자
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Boolean isPublic; // 게시글 공개 여부
    private Long categoryId; // 게시글 카테고리
    private List<String> hashtagList; // 게시글 hashtag
}
