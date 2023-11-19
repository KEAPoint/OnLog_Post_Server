package keapoint.onlog.post.dto.comment.like;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeDto {
    private UUID blogId; // 블로그 식별자
    private UUID commentId; // 댓글 식별자
    private boolean isLiked; // 사용자가 해당 댓글에 대해 '좋아요' 상태인지 나타내는 플래그

    public CommentLikeDto(Blog blog, Comment comment, boolean isLiked) {
        this.blogId = blog.getBlogId();
        this.commentId = comment.getCommentId();
        this.isLiked = isLiked;
    }
}
