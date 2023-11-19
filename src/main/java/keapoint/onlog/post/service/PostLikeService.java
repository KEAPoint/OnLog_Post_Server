package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.post.like.PostLikeDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.entity.UserPostLike;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.PostRepository;
import keapoint.onlog.post.repository.UserPostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeService {

    private final BlogRepository blogRepository;

    private final PostRepository postRepository;

    private final UserPostLikeRepository likeRepository;

    /**
     * 게시글 좋아요, 좋아요 취소
     *
     * @param blogId      좋아요/좋아요 취소를 원하는 블로그 식별자
     * @param postId      좋아요/좋아요 취소 할 게시글 식별자
     * @return 성공 여부
     */
    public PostLikeDto toggleLike(UUID blogId, UUID postId) throws BaseException {
        try {
            // 사용자 정보 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("게시글 좋아요 수정하는 사용자 정보: " + blog.toString());

            // 게시글 정보 조회
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));
            log.info("좋아요 정보 수정 요청 온 게시글 정보: " + post.toString());

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);
            log.info("좋아요 정보 수정하고자 하는 게시글 정보: " + post);

            // 게시글 좋아요 여부를 조회한다
            Optional<UserPostLike> userPostLike = likeRepository.findByBlogAndPost(blog, post);
            boolean isLiked = userPostLike.isPresent();
            log.info("사용자 게시글 좋아요 정보: " + isLiked);

            if (isLiked) { // 게시글 좋아요 했던 상태라면
                likeRepository.delete(userPostLike.get()); // DB에서 삭제하고
                post.postUnlike(); // 게시글 좋아요 개수를 줄여준다.

            } else { // 게시글 좋아요 하지 않았던 상태라면
                UserPostLike like = UserPostLike.builder()
                        .blog(blog)
                        .post(post)
                        .build();

                likeRepository.save(like); // DB에 추가하고
                post.postLike(); // 게시글 좋아요 개수를 늘려준다.
            }

            // 업데이트 된 게시글 정보 로깅
            log.info("업데이트 된 게시글 정보: " + post);

            return new PostLikeDto(blog, post, !isLiked);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
