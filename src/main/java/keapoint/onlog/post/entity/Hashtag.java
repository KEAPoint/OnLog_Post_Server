package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id", nullable = false)
    private Long id;

    @Column(name = "hashtag_name", length = 100, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "hashtagList")
    private List<Post> postList;

    public Hashtag(String name) {
        this.name = name;
        this.postList = new ArrayList<>();
    }
}
