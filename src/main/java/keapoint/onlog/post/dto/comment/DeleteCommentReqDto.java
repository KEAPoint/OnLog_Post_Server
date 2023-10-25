package keapoint.onlog.post.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteCommentReqDto {
    private UUID commentId; // 댓글 식별자
}
