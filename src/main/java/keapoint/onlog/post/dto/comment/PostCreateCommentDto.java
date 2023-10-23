package keapoint.onlog.post.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class PostCreateCommentDto {
    private UUID postId; // 게시글 id
    private String content; // 댓글 내용
    private UUID parentNum; // 부모댓글의 ID (null 가능)
}
