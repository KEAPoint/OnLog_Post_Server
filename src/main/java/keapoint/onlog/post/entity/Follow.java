package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "follow")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", referencedColumnName = "blog_id")
    private Blog follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", referencedColumnName = "blog_id")
    private Blog following;
}
