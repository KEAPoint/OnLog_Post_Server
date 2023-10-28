package keapoint.onlog.post.dto.blog;

import lombok.Data;

import java.util.UUID;

@Data
public class PostFollowReqDto {
    private UUID targetBlogId;
}
