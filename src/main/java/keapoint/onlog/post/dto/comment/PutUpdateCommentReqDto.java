package keapoint.onlog.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutUpdateCommentReqDto {
    private UUID commentId; // 댓글 식별자
    private String content; // 댓글 내용
}
