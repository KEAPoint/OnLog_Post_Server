package keapoint.onlog.post.dto.post;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import keapoint.onlog.post.entity.Post;

import java.util.List;

public class PostWithRelatedPostsDto {

    @JsonUnwrapped
    private PostDto data; // 사용자가 조회한 게시글

    private List<PostSummaryDto> relatedPosts; // 같은 카테고리의 다른 게시글들

    public PostWithRelatedPostsDto(Post post, boolean isLiked) {
        this.data = new PostDto(post, isLiked);
        this.relatedPosts = post.getCategory().getPosts()
                .stream()
                .map(PostSummaryDto::new)
                .toList();
    }

}
