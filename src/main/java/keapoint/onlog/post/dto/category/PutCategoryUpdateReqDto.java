package keapoint.onlog.post.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PutCategoryUpdateReqDto {
    @Schema(description = "카테고리 식별자", type = "Long", example = "5")
    private Long id;

    @Schema(description = "카테고리 이름", type = "string", example = "승현이의 삶")
    private String name;
}
