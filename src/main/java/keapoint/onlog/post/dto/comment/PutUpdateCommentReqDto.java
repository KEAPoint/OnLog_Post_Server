package keapoint.onlog.post.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class PutUpdateCommentReqDto {
    private UUID commentId; // 댓글 식별자
    private String content; // 댓글 내용
}
