package keapoint.onlog.post.dto.post.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePostLikeReqDto {
    private UUID postId;
}
