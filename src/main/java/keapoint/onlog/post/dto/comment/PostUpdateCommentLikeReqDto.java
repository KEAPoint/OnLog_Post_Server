package keapoint.onlog.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostUpdateCommentLikeReqDto {

    private UUID CommentId;

    private boolean targetStatus;

    public boolean getTargetStatus() {
        return targetStatus;
    }
}
