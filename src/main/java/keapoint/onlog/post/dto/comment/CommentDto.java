package keapoint.onlog.post.dto.comment;

import keapoint.onlog.post.entity.Comment;
import lombok.Data;

import java.util.UUID;

@Data
public class CommentDto {
    private UUID commentId; // 댓글 식별자
    private String content; // 댓글 내용
    private Boolean modified; // 댓글 수정 여부
    private long ref; // 그룹
    private long refOrder; // 그룹 순서
    private long step; // 댓글의 계층
    private UUID parentNum; // 부모댓글의 ID
    private long answerNum; // 해당댓글의 자식댓글의 수
    private UUID postId; // 댓글이 달린 게시글 식별자
    private UUID writerId; // 댓글 작성자의 블로그 식별자

    public CommentDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.modified = comment.getModified();
        this.ref = comment.getRef();
        this.refOrder = comment.getRefOrder();
        this.step = comment.getStep();
        this.parentNum = comment.getParentNum();
        this.answerNum = comment.getAnswerNum();
        this.postId = comment.getPost().getPostId();
        this.writerId = comment.getWriter().getBlogId();
    }
}
