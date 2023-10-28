package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final BlogRepository blogRepository;

    private final PostRepository postRepository;

    private final UserPostLikeRepository likeRepository;

    /**
     * 좋아요 추가/제거 처리 서비스 로직
     *
     * @param blogId      사용자 블로그 식별자
     * @param postId      게시글 식별자
     * @param targetValue 게시글 좋아요/싫어요 여부
     * @return 결과
     */
    @Transactional
    public Boolean toggleLike(UUID blogId, UUID postId, Boolean targetValue) throws BaseException {
        try {
            // 사용자 정보 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 게시글 정보 조회
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            UserPostLike userPost = likeRepository.findByBlogAndPost(blog, post)
                    .orElseGet(() -> {
                        UserPostLike newLike = UserPostLike.builder()
                                .blog(blog)
                                .post(post)
                                .isLiked(false) // 기존에 좋아요 한 기록이 없으면 좋아요X 상태
                                .build();

                        return likeRepository.save(newLike); // 새로운 '좋아요' 정보 생성 및 저장
                    });

            userPost.updateLike(targetValue);

            return true;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
