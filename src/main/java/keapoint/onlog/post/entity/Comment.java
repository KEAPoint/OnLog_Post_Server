package keapoint.onlog.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID commentId; // 댓글 식별자

    @Column(name = "comment_content", nullable = false, columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column(name = "comment_modified", nullable = false)
    private Boolean modified; // 댓글 수정 여부

    @Column(name = "comment_ref", nullable = false)
    private long ref; // 그룹

    @Column(name = "comment_ref_order", nullable = false)
    private long refOrder; // 그룹 순서

    @Column(name = "comment_step", nullable = false)
    private long step; // 댓글의 계층

    @Column(name = "comment_parent_num")
    private UUID parentNum; // 부모댓글의 ID

    @Column(name = "comment_answer_num", nullable = false)
    private long answerNum; // 해당댓글의 자식댓글의 수

    @Column(name = "comment_likes_count", nullable = false)
    private Long likesCount; // 댓글 좋아요 갯수

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post; // 댓글이 달린 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog writer; // 댓글 작성자의 블로그

    private void setPost(Post post) {
        this.post = post;
    }

    private void setContent(String content) {
        this.content = content;
    }

    /**
     * 댓글 좋아요
     */
    public void commentLike() {
        this.likesCount += 1;
    }

    /**
     * 댓글 좋아요 취소
     */
    public void commentUnlike() {
        this.likesCount -= 1;
    }

    /**
     * 댓글 작성
     *
     * @param post
     */
    public void addComment(Post post) {
        post.getComments().add(this);
        this.setPost(post);
    }

    /**
     * 댓글 삭제
     */
    public void removeComment() {
        post.getComments().remove(this);
        this.setPost(null);
    }

    public void updateNumberOfChildComment() {
        this.answerNum += 1;
    }

    public void updateComment(String newContent) {
        this.setContent(newContent);
    }
}