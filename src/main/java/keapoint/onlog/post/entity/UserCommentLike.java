package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_comment_like")
public class UserCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Override
    public String toString() {
        return "UserCommentLike{" +
                "id=" + id +
                ", blog=" + blog.getBlogId() +
                ", comment=" + comment.getCommentId() +
                '}';
    }
}
