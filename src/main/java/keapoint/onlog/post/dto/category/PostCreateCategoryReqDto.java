package keapoint.onlog.post.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PostCreateCategoryReqDto {
    @Schema(description = "카테고리 이름", type = "string", example = "승현이의 일상")
    private String name;
}
