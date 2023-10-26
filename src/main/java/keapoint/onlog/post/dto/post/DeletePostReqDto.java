package keapoint.onlog.post.dto.post;

import lombok.Data;

import java.util.UUID;

@Data
public class DeletePostReqDto {
    private UUID postId; // 게시글 식별자
}
