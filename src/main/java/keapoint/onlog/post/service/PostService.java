package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.post.GetPostResDto;
import keapoint.onlog.post.dto.post.GetPostListResDto;
import keapoint.onlog.post.entity.*;
import keapoint.onlog.post.repository.HashtagRepository;
import keapoint.onlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;

    /**
     * 최신 게시글 조회
     *
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPosts(Pageable pageable) {
        // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

        // 게시글을 조회하고 조회된 게시글들을 DTO로 변환하여 반환한다.
        return postRepository.findByStatusAndIsPublic(true, true, sortedByUpdatedDateDesc)
                .map(GetPostListResDto::fromPost);
    }

    /**
     * 주제별 최신 게시글 조회
     *
     * @param topicName 주제 이름
     * @param pageable  페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주제별 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPostsByTopicName(String topicName, Pageable pageable) {
        // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

        // 게시글을 조회하고 조회된 게시글들을 DTO로 변환하여 반환한다.
        return postRepository.findByStatusAndIsPublicAndCategoryTopicName(true, true, topicName, sortedByUpdatedDateDesc)
                .map(GetPostListResDto::fromPost);
    }

    /**
     * 해시태그별 최신 게시글 조회
     *
     * @param hashtag  검색할 해시태그 이름
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 해시태그별 최신 게시글
     * @throws BaseException HASHTAG_NOT_FOUND_EXCEPTION
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getPostsByHashtag(String hashtag, Pageable pageable) throws BaseException {
        // 해시태그 이름으로 해시태그 엔티티를 조회한다.
        Hashtag tag = hashtagRepository.findByName(hashtag)
                .orElseThrow(() -> new BaseException(BaseErrorCode.HASHTAG_NOT_FOUND_EXCEPTION));

        // 페이지 요청 정보에 정렬 조건을 추가하여 새로운 Pageable 객체를 생성한다.
        // 이때 정렬 조건은 'updatedAt' 필드의 내림차순이다.
        Pageable sortedByUpdatedDateDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("updatedAt").descending()
        );

        // 조회된 해시태그가 포함된 모든 게시글을 DTO로 변환한 후 리스트로 만든다.
        List<GetPostListResDto> sortedPosts = tag.getPostList()
                .stream()
                .map(GetPostListResDto::fromPost)
                .toList();

        // 변환된 게시글 리스트와 페이지 요청 정보를 사용하여 새로운 Page 객체를 생성하고 반환한다.
        return new PageImpl<>(sortedPosts, sortedByUpdatedDateDesc, sortedPosts.size());
    }

    /**
     * 특정 게시글 조회
     *
     * @param postId 게시글 식별자
     * @return 게시글
     * @throws BaseException POST_NOT_FOUND_EXCEPTION
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
