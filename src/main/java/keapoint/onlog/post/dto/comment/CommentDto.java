package keapoint.onlog.post.dto.comment;

import keapoint.onlog.post.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private UUID commentId; // 댓글 식별자
    private String content; // 댓글 내용
    private Boolean modified; // 댓글 수정 여부
    private long ref; // 그룹
    private long refOrder; // 그룹 순서
    private long step; // 댓글의 계층
    private UUID parentCommentId; // 부모댓글의 ID
    private long answerNum; // 해당댓글의 자식댓글의 수
    private LocalDateTime createdAt; // 댓글 작성 시간
    private UUID postId; // 댓글이 달린 게시글 식별자
    private UUID writerId; // 댓글 작성자의 블로그 식별자
    private Long likesCount; // 댓글 좋아요 갯수

    public CommentDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.modified = comment.getModified();
        this.ref = comment.getRef();
        this.refOrder = comment.getRefOrder();
        this.step = comment.getStep();
        this.parentCommentId = comment.getParentNum();
        this.answerNum = comment.getAnswerNum();
        this.createdAt = comment.getCreatedAt();
        this.postId = comment.getPost().getPostId();
        this.writerId = comment.getWriter().getBlogId();
        this.likesCount = comment.getLikesCount();
    }
}
