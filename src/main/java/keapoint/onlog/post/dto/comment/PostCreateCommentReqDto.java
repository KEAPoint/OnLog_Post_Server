package keapoint.onlog.post.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateCommentReqDto {
    private UUID postId; // 게시글 id
    private String content; // 댓글 내용
    private UUID parentCommentId; // 부모댓글의 ID (null 가능)
}
