package keapoint.onlog.post.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePostReqDto {
    private UUID postId; // 게시글 식별자
}
