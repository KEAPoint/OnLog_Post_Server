package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_comment_like")
public class UserCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", referencedColumnName = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private boolean isLiked; // 사용자가 해당 댓글에 대해 '좋아요' 상태인지 나타내는 플래그

    @Builder
    public UserCommentLike(Blog blog, Comment comment, boolean isLiked) {
        this.blog = blog;
        this.comment = comment;
        this.isLiked = isLiked;
    }

    /**
     * 댓글 좋아요 업데이트 (좋아요 X <-> 좋아요)
     */
    public void updateLike(Boolean target) throws BaseException {
        if (this.isLiked == target) {
            throw new BaseException(BaseErrorCode.EXPECTED_LIKE_STATE_EXCEPTION);
        } else {
            this.isLiked = target;
        }
    }

}
