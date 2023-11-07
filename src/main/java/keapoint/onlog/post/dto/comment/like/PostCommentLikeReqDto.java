package keapoint.onlog.post.dto.comment.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentLikeReqDto {
    private UUID CommentId;
}
