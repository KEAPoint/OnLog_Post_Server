package keapoint.onlog.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Entity
@ToString
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
    private int ref; // 그룹

    @Column(name = "comment_ref_order", nullable = false)
    private int refOrder; // 그룹 순서

    @Column(name = "comment_step", nullable = false)
    private int step; // 댓글의 계층

    @Column(name = "comment_parent_num", nullable = false)
    private UUID parentNum; // 부모댓글의 ID

    @Column(name = "comment_answer_num", nullable = false)
    private int answerNum; // 해당댓글의 자식댓글의 수

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post; // 댓글이 달린 게시글

    public void setPost(Post post) {
        this.post = post;
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
     *
     * @param comment
     */
    public void removeComment(Comment comment) {
        post.getComments().remove(comment);
        this.setPost(null);
    }
}