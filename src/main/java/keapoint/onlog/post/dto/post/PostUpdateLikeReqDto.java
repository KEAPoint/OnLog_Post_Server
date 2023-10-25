package keapoint.onlog.post.dto.post;

import lombok.Data;

import java.util.UUID;

@Data
public class PostUpdateLikeReqDto {
    private UUID postId;
    private boolean targetStatus;

    public boolean getTargetStatus() {
        return targetStatus;
    }
}
