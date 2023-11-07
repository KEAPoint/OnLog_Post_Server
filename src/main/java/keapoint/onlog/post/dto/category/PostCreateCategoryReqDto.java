package keapoint.onlog.post.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateCategoryReqDto {
    private String name; // 카테고리 이름
}
