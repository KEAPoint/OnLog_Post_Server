package keapoint.onlog.post.dto.blog.follow;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteFollowReqDto {
    private UUID targetBlogId;
}
