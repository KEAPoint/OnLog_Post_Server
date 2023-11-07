package keapoint.onlog.post.dto.blog.follow;

import keapoint.onlog.post.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogFollowDto {
    private UUID blogId;
    private UUID followId;
    private boolean isFollowing; // 해당 블로그를 팔로잉 하고 있는지 여부

    public BlogFollowDto(Follow follow) {
        this.blogId = follow.getMe().getBlogId();
        this.followId = follow.getTarget().getBlogId();
        this.isFollowing = follow.isFollowing();
    }
}
