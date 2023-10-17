package keapoint.onlog.post.service;

import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.post.GetRecentPostResDto;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Page<GetRecentPostResDto> getRecentPosts(Pageable pageable) {
        // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updated_at").descending());

        // 게시글을 조회한다.
        Page<Post> posts = postRepository.findByStatusAndIsPublic(true, true, sortedByUpdatedDateDesc);

        // 조회된 게시글들을 GetRecentPostResDto로 변환하여 반환한다.
        return posts.map(this::convertToDto);
    }

    private GetRecentPostResDto convertToDto(Post post) {
        return GetRecentPostResDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .summary(post.getSummary())
                .thumbnailLink(post.getThumbnailLink())
                .modified(post.getModified())
                .category(post.getCategory().getName())
                .blog(BlogDto.builder()
                        .blogId(post.getBlog().getBlogId())
                        .blogName(post.getBlog().getBlogName())
                        .blogNickname(post.getBlog().getBlogNickname())
                        .blogProfileImg(post.getBlog().getBlogProfileImg())
                        .build())
                .build();
    }

}
