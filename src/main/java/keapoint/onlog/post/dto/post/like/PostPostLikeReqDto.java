package keapoint.onlog.post.dto.post.like;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostPostLikeReqDto {
    private UUID postId;
}
