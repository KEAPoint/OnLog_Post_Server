package keapoint.onlog.post.dto.blog.follow;

import lombok.Data;

import java.util.UUID;

@Data
public class PostFollowReqDto {
    private UUID targetBlogId;
}
