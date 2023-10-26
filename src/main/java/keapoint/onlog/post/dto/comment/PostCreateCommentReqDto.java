package keapoint.onlog.post.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class PostCreateCommentReqDto {
    private UUID postId; // 게시글 id
    private String content; // 댓글 내용
    private UUID parentCommentId; // 부모댓글의 ID (null 가능)
}
