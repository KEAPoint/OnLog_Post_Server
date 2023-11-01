package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.post.*;
import keapoint.onlog.post.entity.*;
import keapoint.onlog.post.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPosts(Pageable pageable) throws BaseException {
        try {
            // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
            Pageable sortedByUpdatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("updatedAt").descending());

            // 게시글을 조회하고 조회된 게시글들을 DTO로 변환하여 반환한다.
            return postRepository.findByStatusAndIsPublic(true, true, sortedByUpdatedDateDesc)
                    .map(GetPostListResDto::new);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 주제별 최신 게시글 조회
     *
     * @param topicName 주제 이름
     * @param pageable  페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주제별 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getRecentPostsByTopicName(String topicName, Pageable pageable) throws BaseException {
        try {
            // 주제 이름으로 주제 엔티티를 조회한다.
            Topic topic = topicRepository.findByName(topicName)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));

            // 수정일자를 기준으로 내림차순 정렬 조건을 적용한 Pageable 객체를 생성한다.
            Pageable sortedByUpdatedDateDesc = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("updatedAt").descending()
            );

            // 조회된 주제가 포함된 모든 게시글을 DTO로 변환한 후 리스트로 만든다.
            List<GetPostListResDto> sortedPosts = postRepository.findByStatusAndIsPublicAndTopicName(true, true, topic.getName(), sortedByUpdatedDateDesc)
                    .stream()
                    .map(GetPostListResDto::new)
                    .toList();

            // 변환된 게시글 리스트와 페이지 요청 정보를 사용하여 새로운 Page 객체를 생성하고 반환한다.
            return new PageImpl<>(sortedPosts, sortedByUpdatedDateDesc, sortedPosts.size());

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 해시태그별 최신 게시글 조회
     *
     * @param hashtag  검색할 해시태그 이름
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 해시태그별 최신 게시글
     */
    @Transactional(readOnly = true)
    public Page<GetPostListResDto> getPostsByHashtag(String hashtag, Pageable pageable) throws BaseException {
        try {
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
                    .map(GetPostListResDto::new)
                    .toList();

            // 변환된 게시글 리스트와 페이지 요청 정보를 사용하여 새로운 Page 객체를 생성하고 반환한다.
            return new PageImpl<>(sortedPosts, sortedByUpdatedDateDesc, sortedPosts.size());

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 게시글 조회
     *
     * @param postId 게시글 식별자
     * @return 게시글
     */
    public GetPostResDto getPost(UUID postId) throws BaseException {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            post.hit();

            List<String> hashtags = post.getHashtagList().stream()
                    .map(Hashtag::getName)
                    .toList();

            BlogDto blog = new BlogDto(post.getWriter());

            return GetPostResDto.builder().post(post).hashtags(hashtags).blog(blog).build();

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
     * @param blogId 블로그 식별자
     * @param dto    작성하고자 하는 게시글 정보
     * @return 게시글
     */
    public PostDto writePost(UUID blogId, PostWritePostReqDto dto) throws BaseException {
        try {
            // 카테고리를 조회한다.
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 해당 카테고리 주인인지 확인한다.
            if (!category.getCategoryOwner().getBlogId().equals(blogId))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

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

            // 게시글 작성자의 블로그를 조회한다
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 게시글을 만든다
            Post post = Post.builder()
                    .postHits(0L)
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .summary(dto.getSummary())
                    .thumbnailLink(dto.getThumbnailLink())
                    .isPublic(dto.getIsPublic())
                    .modified(false)
                    .category(category)
                    .hashtagList(hashtagList)
                    .comments(new ArrayList<>())
                    .writer(writer)
                    .build();

            // DB에 저장한다.
            Post savedPost = postRepository.save(post);

            // Todo: 양방향 연관관계 객체 필드 설정

            // 생성된 게시글 정보를 반환한다.
            return new PostDto(savedPost);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public PostDto modifyPost(UUID blogId, PutModifyPostReqDto dto) throws BaseException {
        try {
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
            if (!category.getCategoryOwner().getBlogId().equals(blogId))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

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
            post.modifyPost(dto, category, hashtagList);

            // Todo: 양방향 연관관계 객체 필드 설정

            // 수정된 게시글 정보를 반환한다.
            return new PostDto(post);

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
     * @param blogId 블로그 식별자
     * @param dto    삭제하고자 하는 게시글 식별자가 들어있는 객체
     * @return 게시글 삭제 성공 여부
     */
    public DeletePostResDto deletePost(UUID blogId, DeletePostReqDto dto) throws BaseException {
        try {
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            if (!post.getWriter().getBlogId().equals(blogId)) { // 게시글 작성자가 아니라면
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION); // permission exception
            }

            postRepository.delete(post); // 게시글 삭제

            return new DeletePostResDto(true); // 결과 return

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
