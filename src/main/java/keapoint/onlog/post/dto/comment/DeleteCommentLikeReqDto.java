package keapoint.onlog.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteCommentLikeReqDto {
    private UUID CommentId;
}
