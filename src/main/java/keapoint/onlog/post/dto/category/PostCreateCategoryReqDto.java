package keapoint.onlog.post.dto.category;

import lombok.Data;

@Data
public class PostCreateCategoryReqDto {
    private String name; // 카테고리 이름
    private Long topicId; // 해당 카테고리와 연관된 주제 식별자
}
