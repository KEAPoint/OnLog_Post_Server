package keapoint.onlog.post.dto.category;

import keapoint.onlog.post.entity.Category;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private int order;
    private String topic;

    public CategoryDto() {
    }

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.order = category.getOrder();
    }

    public static CategoryDto fromEntity(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setOrder(category.getOrder());
        return categoryDto;
    }
}
