package keapoint.onlog.post.dto.blog;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteFollowReqDto {
    private UUID targetBlogId;
}
