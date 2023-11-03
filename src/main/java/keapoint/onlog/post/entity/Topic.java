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
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false)
    private Long id;

    @Column(name = "topic_name", nullable = false, length = 20)
    private String name;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    public Topic(String name) {
        this.name = name;
    }
}
