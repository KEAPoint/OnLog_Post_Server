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
     * @param targetValue 댓글 좋아요/좋아요 취소 여부
     * @return 성공 여부
     */
    public CommentLikeDto toggleLike(UUID blogId, UUID commentId, Boolean targetValue) throws BaseException {
        try {
            // 사용자 정보 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("댓글 좋아요 수정하는 사용자 정보: " + blog.toString());

            // 댓글 정보 조회
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
            log.info("댓글 정보: " + comment.toString());

            // 댓글 좋아요 정보 조회
            UserCommentLike userCommentLike = userCommentLikeRepository.findByBlogAndComment(blog, comment)
                    .orElseGet(() -> {
                        UserCommentLike newLike = UserCommentLike.builder()
                                .blog(blog)
                                .comment(comment)
                                .isLiked(false) // 기존에 좋아요 한 기록이 없으면 좋아요X 상태
                                .build();

                        return userCommentLikeRepository.save(newLike); // 새로운 '좋아요' 정보 생성 및 저장
                    });
            log.info("사용자 댓글 좋아요 정보: " + userCommentLike);

            // 댓글 좋아요 정보 업데이트
            userCommentLike.updateLike(targetValue);
            log.info("업데이트 된 사용자 댓글 좋아요 정보: " + userCommentLike);

            if (Boolean.TRUE.equals(targetValue)) { // 댓글 좋아요라면
                comment.commentLike(); // 댓글 좋아요 개수 늘려주고
            } else { // 댓글 좋아요 취소라면
                comment.commentUnlike(); // 댓글 좋아요 개수 줄여준다.
            }

            // 업데이트 된 댓글 정보 로깅
            log.info("업데이트 된 댓글 정보: " + comment);

            // 결과 리턴
            return new CommentLikeDto(userCommentLike);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
