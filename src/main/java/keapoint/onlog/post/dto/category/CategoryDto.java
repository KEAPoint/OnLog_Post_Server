package keapoint.onlog.post.dto.category;

import keapoint.onlog.post.entity.Category;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private int order;
    private String topic;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.order = category.getOrder();
        this.topic = category.getTopic().getName();
    }
}
