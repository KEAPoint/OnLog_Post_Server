package keapoint.onlog.post.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostPostLikeReqDto {
    private UUID postId;
}
