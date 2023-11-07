package keapoint.onlog.post.dto.category;

import keapoint.onlog.post.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private int order;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.order = category.getOrder();
    }
}
