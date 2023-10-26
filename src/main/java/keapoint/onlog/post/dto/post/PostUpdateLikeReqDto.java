package keapoint.onlog.post.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostUpdateLikeReqDto {
    private UUID postId;
    private boolean targetStatus;

    public boolean getTargetStatus() {
        return targetStatus;
    }
}
