package keapoint.onlog.post.entity;

import jakarta.persistence.*;
import keapoint.onlog.post.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(name = "category_name", nullable = false, length = 16)
    private String name;

    @Column(name = "category_order", nullable = false)
    private int order;

    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    public void updateCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}