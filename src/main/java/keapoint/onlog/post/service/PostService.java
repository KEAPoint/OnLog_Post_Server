package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.post.*;
import keapoint.onlog.post.entity.*;
import keapoint.onlog.post.repository.*;
import keapoint.onlog.post.specification.PostSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final HashtagRepository hashtagRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 최신 게시글 조회
     *
     * @param myBlogId   내 블로그 식별자
     * @param topicName  (필터 1) 주제
     * @param hashtag    (필터 2) 해시태그
     * @param blogId     (필터 3) 블로그
     * @param categoryId (필터 4) 카테고리
     * @param isPublic   (필터 5) 게시글 공개 여부
     * @param pageable   페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getRecentPosts(UUID myBlogId, String topicName, String hashtag, UUID blogId, Long categoryId, Boolean isPublic, Pageable pageable) throws BaseException {
        try {
            if (!myBlogId.equals(blogId) && Boolean.FALSE.equals(isPublic)) // 조회하는 비공개 게시글이 내 블로그가 아닌 경우
                throw new BaseException(BaseErrorCode.ACCESS_DENIED_EXCEPTION); // ACCESS_DENIED_EXCEPTION을 터트린다.

            Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

            Specification<Post> specification = Specification.where(PostSpecification.withStatusTrue())
                    .and(PostSpecification.withTopicName(topicName))
                    .and(PostSpecification.withHashtag(hashtag))
                    .and(PostSpecification.withBlogId(blogId))
                    .and(PostSpecification.withCategoryId(categoryId))
                    .and(PostSpecification.withIsPublic(isPublic));

            return postRepository.findAll(specification, sortedByUpdatedDateDesc)
                    .map(PostSummaryDto::new);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게시글 조회
     *
     * @param postId 조회하고자 하는 게시글 식별자
     * @return 조회된 게시글 정보
     */
    public PostWithRelatedPostsDto getPost(UUID postId) throws BaseException {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            post.hit();

            return new PostWithRelatedPostsDto(post);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 비공개 게시글 조회
     *
     * @param blogId   비공개 게시글을 조회하고자 하는 블로그 식별자
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 조회된 비공개 게시글
     */
    public Page<PostSummaryDto> getPrivatePosts(UUID blogId, Pageable pageable) throws BaseException {
        try {
            // 블로그 식별자를 기반으로 내 블로그를 조회한다.
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 페이지 요청 정보에 정렬 조건을 추가하여 새로운 Pageable 객체를 생성한다.
            // 이때 정렬 조건은 'updatedAt' 필드의 내림차순이다.
            Pageable sortedByUpdatedDateDesc = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("updatedAt").descending()
            );

            // 조회된 비공개 게시글을 DTO로 변환하여 반환한다.
            return postRepository.findByWriterAndStatusAndIsPublic(writer, true, false, sortedByUpdatedDateDesc)
                    .map(PostSummaryDto::new);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게시글 작성
     *
     * @param blogId 게시글 작성을 원하는 블로그 식별자
     * @param dto    작성하고자 하는 게시글 정보
     * @return 작성된 게시글 정보
     */
    public PostSummaryDto writePost(UUID blogId, PostWritePostReqDto dto) throws BaseException {
        try {
            // 게시글을 작성하고자 하는 사용자를 조회한다.
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 카테고리를 조회한다.
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 해당 카테고리 주인인지 확인한다.
            if (writer.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 주제를 조회한다.
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));

            // 해시태그를 조회한다. 만약 해시태그가 없는 경우엔 만든다
            List<Hashtag> hashtagList = dto.getHashtagList()
                    .stream()
                    .map(hashtag -> hashtagRepository.findByName(hashtag)
                            .orElseGet(() -> {
                                Hashtag newHashtag = new Hashtag(hashtag);
                                return hashtagRepository.save(newHashtag);
                            })
                    )
                    .toList();

            // 게시글을 만든다
            Post post = new Post(dto, writer, category, topic, hashtagList);

            // DB에 저장한다.
            Post savedPost = postRepository.save(post);
            log.info("생성된 게시글: " + savedPost.getPostId());

            // 생성된 게시글 정보를 반환한다.
            return new PostSummaryDto(savedPost);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 게시글 수정
     *
     * @param blogId 게시글 수정을 원하는 블로그 식별자
     * @param dto    수정하고자 하는 게시글 정보
     * @return 수정된 게시글 정보
     */
    public PostSummaryDto modifyPost(UUID blogId, PutModifyPostReqDto dto) throws BaseException {
        try {
            // 게시글을 수정하고자 하는 사용자를 조회한다.
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 수정하고자 하는 게시글을 조회한다.
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            // 게시글이 삭제되었는지 확인한다. 삭제된 경우 post not found exception을 던진다
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);

            // 게시글 작성자인지 확인한다.
            if (!post.getWriter().getBlogId().equals(blogId))
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION);

            // 카테고리를 조회한다.
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 해당 카테고리 주인인지 확인한다.
            if (writer.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 주제를 조회한다.
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));

            // 해시태그를 조회한다. 만약 해시태그가 없는 경우엔 만든다
            List<Hashtag> hashtagList = dto.getHashtagList()
                    .stream()
                    .map(hashtag -> hashtagRepository.findByName(hashtag)
                            .orElseGet(() -> {
                                Hashtag newHashtag = new Hashtag(hashtag);
                                return hashtagRepository.save(newHashtag);
                            })
                    )
                    .toList();

            // 게시글을 수정한다
            post.modifyPost(dto, category, topic, hashtagList);

            // 수정된 게시글 정보를 반환한다.
            return new PostSummaryDto(post);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 게시글 삭제
     *
     * @param blogId 게시글 삭제를 원하는 블로그 식별자
     * @param dto    삭제하고자 하는 게시글 정보
     * @return 삭제된 게시글 정보
     */
    public PostSummaryDto deletePost(UUID blogId, DeletePostReqDto dto) throws BaseException {
        try {
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            if (!post.getWriter().getBlogId().equals(blogId)) { // 게시글 작성자가 아니라면
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION); // permission exception
            }

            // 게시글을 삭제한다.
            post.deletePost();

            // DB에서 게시글 삭제한다.
            postRepository.delete(post);

            return new PostSummaryDto(post); // 결과 return

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
