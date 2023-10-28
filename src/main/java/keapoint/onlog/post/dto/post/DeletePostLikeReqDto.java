package keapoint.onlog.post.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeletePostLikeReqDto {
    private UUID postId;
}
