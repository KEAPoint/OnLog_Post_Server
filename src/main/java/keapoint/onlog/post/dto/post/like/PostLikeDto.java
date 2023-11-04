package keapoint.onlog.post.dto.post.like;

import keapoint.onlog.post.entity.UserPostLike;
import lombok.*;

import java.util.UUID;

@Data
public class PostLikeDto {
    private UUID blogId; // 블로그 식별자
    private UUID postId; // 게시글 식별자
    private boolean isLiked; // 사용자가 해당 게시글에 대해 '좋아요' 상태인지 나타내는 플래그

    public PostLikeDto(UserPostLike data) {
        this.blogId = data.getBlog().getBlogId();
        this.postId = data.getPost().getPostId();
        this.isLiked = data.isLiked();
    }
}

