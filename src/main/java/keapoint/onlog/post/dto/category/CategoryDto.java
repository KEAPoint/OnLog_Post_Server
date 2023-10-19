package keapoint.onlog.post.dto.category;

import keapoint.onlog.post.entity.Category;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private int order;
    private String topic;

    public CategoryDto fromCategory(Category category) {
        CategoryDto dto = new CategoryDto();

        dto.id = category.getId();
        dto.name = category.getName();
        dto.order = category.getOrder();
        dto.topic = category.getTopic().getName();

        return dto;
    }
}
