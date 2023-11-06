package keapoint.onlog.post.dto.blog;

import io.swagger.v3.oas.annotations.media.Schema;
import keapoint.onlog.post.entity.Blog;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostCreateBlogReqDto {
    @Schema(description = "사용자 블로그 식별자", type = "string", format = "uuid")
    private UUID blogId;

    @Schema(description = "사용자 블로그 이름", type = "string")
    private String blogName;

    @Schema(description = "사용자 블로그 별명", type = "string")
    private String blogNickname;

    @Schema(description = "사용자 블로그 한 줄 소개", type = "string")
    private String blogIntro;

    @Schema(description = "사용자 블로그 프로필", type = "string")
    private String blogProfileImg;

    public Blog toEntity(UUID blogId) {
        return Blog.builder()
                .blogId(blogId)
                .blogName(blogName)
                .blogNickname(blogNickname)
                .blogIntro(blogIntro)
                .blogProfileImg(blogProfileImg)
                .build();
    }
}
