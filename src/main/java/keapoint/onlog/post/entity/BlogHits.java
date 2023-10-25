package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Entity
public class BlogHits {

    @Id
    @Column(name = "blog_hit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID blogHitsId;

    @Column(name = "date", nullable = false)
    private Timestamp blogVisitDate; // 블로그 방문 날짜

    @Column(name = "blog_id", nullable = false)
    private String blogId; // 방문 블로그 식별자

    @Column(name = "blog_visitor_count", nullable = false)
    private Long blogVisitorCount;
}
