package keapoint.onlog.post.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutCategoryUpdateReqDto {
    private Long id;
    private String name;
}
