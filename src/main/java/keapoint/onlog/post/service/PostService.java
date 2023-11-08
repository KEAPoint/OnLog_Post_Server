package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.comment.CommentDto;
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
    private final UserPostLikeRepository userPostLikeRepository;
    private final UserCommentLikeRepository userCommentLikeRepository;

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
     * @param blogId 내 블로그 식별자
     * @param postId 조회하고자 하는 게시글 식별자
     * @return 조회된 게시글 정보
     */
    public PostWithRelatedPostsDto getPost(UUID blogId, UUID postId) throws BaseException {
        try {
            // 내 블로그를 조회한다.
            Blog me = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 게시글을 조회한다.
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));
            log.info("조회 요청 온 게시글 정보: " + post.toString());

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);
            log.info("조회할 게시글 정보: " + post);

            // 게시글의 권한을 확인한다.
            if (post.getIsPublic().equals(false) && !post.getWriter().equals(me))
                throw new BaseException(BaseErrorCode.ACCESS_DENIED_EXCEPTION);

            post.hit();

            // 내가 해당 게시글을 좋아요 하고 있는지 조회한다.
            UserPostLike userPostLike = userPostLikeRepository.findByBlogAndPost(me, post).orElseGet(() -> {
                UserPostLike newLike = UserPostLike.builder()
                        .blog(me)
                        .post(post)
                        .isLiked(false) // 기존에 좋아요 한 기록이 없으면 좋아요X 상태
                        .build();

                return userPostLikeRepository.save(newLike); // 새로운 '좋아요' 정보 생성 및 저장
            });
            log.info("내 블로그 정보: " + me.toString());
            log.info("게시글 좋아요 정보: " + userPostLike);

            // 게시글의 댓글들에 대해 각각 좋아요 정보를 조회하고 CommentDto를 생성한다.
            List<CommentDto> commentDtoList = new ArrayList<>();
            post.getComments().stream()
                    .filter(Comment::getStatus) // 유효한 (삭제되지 않은) 댓글만 필터링
                    .forEach(comment -> {
                        UserCommentLike userCommentLike = userCommentLikeRepository.findByBlogAndComment(me, comment).orElseGet(() -> {
                            UserCommentLike newLike = UserCommentLike.builder()
                                    .blog(me)
                                    .comment(comment)
                                    .isLiked(false) // 기존에 좋아요 한 기록이 없으면 좋아요X 상태
                                    .build();

                            return userCommentLikeRepository.save(newLike); // 새로운 '좋아요' 정보 생성 및 저장
                        });
                        commentDtoList.add(new CommentDto(comment, userCommentLike.isLiked()));
                    });
            log.info("사용자 댓글 좋아요 정보: " + commentDtoList);

            return new PostWithRelatedPostsDto(post, userPostLike.isLiked(), commentDtoList);

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
            log.info("게시글 작성할 블로그 정보: " + writer.toString());

            // 카테고리를 조회한다.
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
            log.info("게시글 카테고리 정보: " + category.toString());

            // 해당 카테고리 주인인지 확인한다.
            if (!writer.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 주제를 조회한다.
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));
            log.info("게시글 주제 정보: " + topic.toString());

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
            log.info("게시글 해시태그 정보: " + hashtagList);

            // 게시글을 생성한다.
            Post post = postRepository.save(new Post(dto, writer, category, topic, hashtagList));
            log.info("생성된 게시글 정보: " + post);

            // 생성된 게시글 정보를 반환한다.
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
            log.info("게시글 수정할 블로그 정보: " + writer.toString());

            // 수정하고자 하는 게시글을 조회한다.
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));
            log.info("수정 요청 온 게시글 정보: " + post.toString());

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);
            log.info("수정할 게시글 정보: " + post);

            // 게시글 작성자인지 확인한다.
            if (!post.getWriter().equals(writer))
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION);

            // 카테고리를 조회한다.
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
            log.info("게시글 카테고리 정보: " + category.toString());

            // 해당 카테고리 주인인지 확인한다.
            if (!writer.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 주제를 조회한다.
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));
            log.info("게시글 주제 정보: " + topic.toString());

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
            log.info("게시글 해시태그 정보: " + hashtagList);

            // 게시글을 수정한다
            post.modifyPost(dto, category, topic, hashtagList);
            log.info("수정된 게시글 정보: " + post);

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
     */
    public void deletePost(UUID blogId, DeletePostReqDto dto) throws BaseException {
        try {
            // 게시글을 삭제하고자 하는 사용자를 조회한다.
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("삭제할 게시글 정보: " + writer.toString());

            // 삭제하고자 하는 게시글을 조회한다.
            Post post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));
            log.info("삭제 요청 온 게시글 정보: " + post.toString());

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);
            log.info("삭제할 게시글 정보: " + post);

            // 게시글 작성자인지 확인한다.
            if (!post.getWriter().equals(writer))
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION);

            // 게시글 좋아요 정보를 삭제한다.
            userPostLikeRepository.deleteByPost(post);

            // 게시글을 삭제한다.
            post.setStatus(false);
            post.resetPostLike();

            log.info("게시글이 삭제되었습니다.");

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
