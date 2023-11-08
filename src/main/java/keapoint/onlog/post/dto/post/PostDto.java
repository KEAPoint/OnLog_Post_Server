package keapoint.onlog.post.dto.post;

import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.comment.CommentDto;
import keapoint.onlog.post.dto.hashtag.HashtagDto;
import keapoint.onlog.post.dto.topic.TopicDto;
import keapoint.onlog.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private UUID postId; // 게시글 식별자
    private Long postHits; // 게시글 방문 횟수
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String summary; // 게시글 3줄 요약
    private String thumbnailLink; // 게시글 thumbnail 사진 위치
    private Boolean postLiked; // 게시글을 좋아요 하고 있는지 여부
    private Long likesCount; // 게시글 좋아요 갯수
    private Boolean isPublic; // 게시글 공개 여부
    private Boolean isModified; // 게시글 수정 여부
    private TopicDto topic; // 게시글 주제
    private CategoryDto category; // 게시글 카테고리
    private List<HashtagDto> hashtagList; // 해시태그 리스트
    private int commentsCounts; // 게시글 댓글 갯수
    private List<CommentDto> comments; // 게시글 댓글
    private BlogDto writer; // 작성자
    private LocalDateTime createdAt; // Row 생성 시점

    public PostDto(Post post, boolean postLiked, List<CommentDto> commentDtoList) {
        this.postId = post.getPostId();
        this.postHits = post.getPostHits();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.summary = post.getSummary();
        this.thumbnailLink = post.getThumbnailLink();
        this.postLiked = postLiked;
        this.likesCount = post.getLikesCount();
        this.isPublic = post.getIsPublic();
        this.isModified = post.getModified();
        this.topic = new TopicDto(post.getTopic());
        this.category = new CategoryDto(post.getCategory());
        this.hashtagList = post.getHashtagList().stream()
                .map(HashtagDto::new)
                .toList();

        this.commentsCounts = post.getComments().size();
        this.comments = commentDtoList;
        this.writer = new BlogDto(post.getWriter());
        this.createdAt = post.getCreatedAt();
    }
}
