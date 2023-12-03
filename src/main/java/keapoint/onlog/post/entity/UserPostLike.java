package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_post_like")
public class UserPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id")
    private Blog blog; // 내 블로그

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post; // 게시글

    @Override
    public String toString() {
        return "UserPostLike{" +
                "id=" + id +
                ", blog=" + blog.getBlogId() +
                ", post=" + post.getPostId() +
                '}';
    }
}

