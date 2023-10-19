package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.post.GetPostResDto;
import keapoint.onlog.post.dto.post.GetPostListResDto;
import keapoint.onlog.post.entity.*;
import keapoint.onlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPosts(Pageable pageable) {
        // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

        // 게시글을 조회하고 조회된 게시글들을 DTO로 변환하여 반환한다.
        return postRepository.findByStatusAndIsPublic(true, true, sortedByUpdatedDateDesc)
                .map(GetPostListResDto::fromPost);
    }

    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPostsByTopicName(String topicName, Pageable pageable) {
        // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

        // 게시글을 조회하고 조회된 게시글들을 DTO로 변환하여 반환한다.
        return postRepository.findByStatusAndIsPublicAndCategoryTopicName(true, true, topicName, sortedByUpdatedDateDesc)
                .map(GetPostListResDto::fromPost);
    }

    /**
     * 특정 게시글 조회
     *
     * @param postId 게시글 식별자
     * @return
     * @throws BaseException
     */
    public GetPostResDto getPost(UUID postId) throws BaseException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

        post.hit();

        List<String> hashtags = post.getHashtagList().stream()
                .map(Hashtag::getName)
                .toList();

        BlogDto blog = BlogDto.fromBlog(post.getWriter());

        return GetPostResDto.builder().post(post).hashtags(hashtags).blog(blog).build();
    }
}
