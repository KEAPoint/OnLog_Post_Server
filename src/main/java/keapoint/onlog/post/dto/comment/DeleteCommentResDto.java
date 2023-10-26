package keapoint.onlog.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteCommentResDto {
    private Boolean isSuccess; // 댓글 삭제 성공 여부
}
