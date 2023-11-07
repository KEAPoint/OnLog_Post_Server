package keapoint.onlog.post.dto.blog.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFollowReqDto {
    private UUID targetBlogId;
}
