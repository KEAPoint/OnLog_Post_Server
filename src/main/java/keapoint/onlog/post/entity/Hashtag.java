package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id", nullable = false)
    private Long id;

    @Column(name = "hashtag_name", length = 100, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "hashtagList")
    private List<Post> postList = new ArrayList<>();
}
