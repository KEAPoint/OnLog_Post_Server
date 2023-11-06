package keapoint.onlog.post.dto.blog.follow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteFollowReqDto {
    @Schema(description = "사용자가 팔로우 취소할 블로그 식별자", type = "string", format = "uuid", example = "3bed8405-0eb1-4907-bffe-2437112ff671")
    private UUID targetBlogId;
}
