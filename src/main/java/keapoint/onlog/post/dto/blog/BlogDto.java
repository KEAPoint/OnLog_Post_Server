package keapoint.onlog.post.dto.blog;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BlogDto {
    private UUID blogId; // 사용자 블로그 id
    private String blogName; // 사용자 블로그 이름
    private String blogNickname; // 사용자 블로그 별명
    private String blogProfileImg; // 사용자 블로그 프로필
}
