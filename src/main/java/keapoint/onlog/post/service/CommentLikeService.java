package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.comment.like.CommentLikeDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.UserCommentLike;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CommentRepository;
import keapoint.onlog.post.repository.UserCommentLikeRepository;
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
public class CommentLikeService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final UserCommentLikeRepository userCommentLikeRepository;

    /**
     * 댓글 좋아요, 좋아요 취소
     *
     * @param blogId      좋아요/좋아요 취소를 원하는 블로그 식별자
     * @param commentId   좋아요/좋아요 취소 할 댓글 식별자
     * @return 성공 여부
     */
    public CommentLikeDto toggleLike(UUID blogId, UUID commentId) throws BaseException {
        try {
            // 사용자 정보 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("댓글 좋아요 수정하는 사용자 정보: " + blog.toString());

            // 댓글 정보 조회
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
            log.info("좋아요 정보 수정 요청 온 댓글 정보: " + comment.toString());

            // 댓글이 삭제되었는지 확인한다.
            if (comment.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
            log.info("좋아요 정보 수정하고자 하는 댓글 정보: " + comment);

            // 댓글 좋아요 정보를 조회한다
            Optional<UserCommentLike> userCommentLike = userCommentLikeRepository.findByBlogAndComment(blog, comment);
            boolean isLiked = userCommentLike.isPresent();
            log.info("사용자 댓글 좋아요 정보: " + isLiked);

            if (isLiked) { // 댓글 좋아요 했던 상태라면
                userCommentLikeRepository.delete(userCommentLike.get()); // DB에서 삭제하고
                comment.commentUnlike(); // 댓글 좋아요 개수를 줄여준다.

            } else { // 댓글 좋아요 하지 않았던 상태라면
                UserCommentLike like = UserCommentLike.builder()
                        .blog(blog)
                        .comment(comment)
                        .build();

                userCommentLikeRepository.save(like); // DB에 추가하고
                comment.commentLike(); // 댓글 좋아요 개수를 늘려준다.
            }

            // 업데이트 된 댓글 정보 로깅
            log.info("업데이트 된 댓글 정보: " + comment);

            // 결과 리턴
            return new CommentLikeDto(blog, comment, !isLiked);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

