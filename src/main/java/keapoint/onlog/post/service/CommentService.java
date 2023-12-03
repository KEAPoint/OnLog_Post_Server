package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.comment.CommentSummaryDto;
import keapoint.onlog.post.dto.comment.DeleteCommentReqDto;
import keapoint.onlog.post.dto.comment.PostCreateCommentReqDto;
import keapoint.onlog.post.dto.comment.PutUpdateCommentReqDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CommentRepository;
import keapoint.onlog.post.repository.PostRepository;
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
public class CommentService {

    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserCommentLikeRepository userCommentLikeRepository;

    /**
     * 댓글 작성
     *
     * @param blogId 댓글을 작성 할 블로그 식별자
     * @param data   작성 할 댓글 정보
     * @return 댓글 정보
     */
    public CommentSummaryDto createComment(UUID blogId, PostCreateCommentReqDto data) throws BaseException {
        try {
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            Post post = postRepository.findById(data.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);

            long ref; // 그룹
            long refOrder; // 그룹 순서
            long step; // 댓글의 계층
            UUID parentNum = data.getParentCommentId(); // 부모댓글의 ID
            long answerNum = 0L; // 해당댓글의 자식댓글의 수. 댓글이 작성된 경우이므로 0

            if (parentNum != null) { // 부모 댓글이 있는 경우 (대댓글)
                Comment parentComment = commentRepository.findById(data.getParentCommentId())
                        .orElseThrow(() -> new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

                ref = parentComment.getRef(); // 부모와 같은 그룹
                step = parentComment.getStep() + 1L; // 부모의 댓글이므로 댓글의 계층은 부모 계층 + 1
                refOrder = updateAndGetCurrentRefOrder(parentComment);

                parentComment.updateNumberOfChildComment(); // 부모 댓글의 자식 댓글 갯수 update

            } else { // 부모 댓글이 없는 경우 (댓글)
                ref = commentRepository.findMaxRefByPost(post).orElse(1L) + 1; // 첫번째 댓글은 1, 그 외 댓글은 최댓값 + 1
                refOrder = 1L; // 대댓글이 없으므로 그룹 순서는 1
                step = 1L; // 대댓글이 아닌 댓글이므로 계층은 1
            }

            // 댓글 Entity 생성
            Comment comment = Comment.builder()
                    .content(data.getContent())
                    .modified(false)
                    .ref(ref)
                    .refOrder(refOrder)
                    .step(step)
                    .parentNum(parentNum)
                    .answerNum(answerNum)
                    .likesCount(0L)
                    .post(post)
                    .writer(writer)
                    .build();

            // 댓글 저장
            comment.addComment(post);
            commentRepository.save(comment);

            post.writeComment();

            log.info("사용자 (" + blogId + ")가 게시글 (" + post.getPostId() + ")에 댓글(" + comment.getCommentId() +")을 작성하는 데 성공하였습니다.");
            return new CommentSummaryDto(comment);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private long updateAndGetCurrentRefOrder(Comment parentComment) {
        long saveStep = parentComment.getStep() + 1L;
        long refOrder = parentComment.getRefOrder();
        long answerNum = parentComment.getAnswerNum();
        long ref = parentComment.getRef();

        // 부모 댓글 그룹의 자식 수
        long answerNumSum = commentRepository.getSumOfAnswerNum(ref);

        // 부모 댓글 그룹의 최대 step값
        long maxStep = commentRepository.getMaxStep(ref);

        // 저장할 대댓글 step과 그룹내의 최댓값 step 조건 비교
        /*
         * step + 1 < 그룹리스트에서 max step값  AnswerNum sum + 1           * NO UPDATE
         * step + 1 = 그룹리스트에서 max step값  refOrder + AnswerNum + 1    * UPDATE
         * step + 1 > 그룹리스트에서 max step값  refOrder + 1                * UPDATE
         */
        if (saveStep < maxStep) {
            return answerNumSum + 1;

        } else if (saveStep == maxStep) {
            commentRepository.updateRefOrder(ref, refOrder + answerNum);
            return refOrder + answerNum + 1;

        } else {
            commentRepository.updateRefOrder(ref, refOrder);
            return refOrder + 1;
        }
    }

    /**
     * 댓글 수정
     *
     * @param blogId 댓글 수정을 원하는 블로그 식별자
     * @param dto    수정하고자 하는 댓글 정보
     * @return 수정된 댓글 정보
     */
    public CommentSummaryDto updateComment(UUID blogId, PutUpdateCommentReqDto dto) throws BaseException {
        try {
            // 댓글을 수정하려고 하는 블로그를 조회한다.
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("댓글을 수정하려고 하는 블로그 정보: " + blog);

            // 수정할 댓글을 가져온다
            Comment comment = commentRepository.findById(dto.getCommentId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
            log.info("수정 요청 온 댓글: " + comment.toString());

            // 작성된 게시글을 가져온다
            Post post = comment.getPost();
            log.info("댓글 수정 요청 온 게시글 정보: " + post.toString());

            // 게시글이 삭제되었는지 확인한다.
            if (post.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION);
            log.info("댓글이 작성되는 게시글 정보: " + post);

            // 댓글이 삭제되었는지 확인한다.
            if (comment.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
            log.info("수정할 댓글 정보: " + comment);

            // 댓글 수정 권한을 확인한다.
            if (!comment.getWriter().equals(blog)) { // 댓글 작성자가 아니라면
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION); // permission exception
            }

            // 댓글 수정
            comment.updateComment(dto.getContent());
            log.info("수정된 댓글: " + comment);

            return new CommentSummaryDto(comment); // 댓글 dto 반환

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 댓글 삭제
     *
     * @param blogId 댓글 삭제를 원하는 블로그 식별자
     * @param dto    삭제하고자 하는 댓글 정보
     */
    public void deleteComment(UUID blogId, DeleteCommentReqDto dto) throws BaseException {
        try {
            // 댓글을 삭제하려고 하는 블로그를 조회한다.
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("댓글을 삭제하려고 하는 블로그 정보: " + blog);

            // 삭제할 댓글을 조회한다.
            Comment comment = commentRepository.findById(dto.getCommentId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
            log.info("삭제 요청 온 댓글 정보: " + comment.toString());

            // 댓글이 삭제되었는지 확인한다.
            if (comment.getStatus().equals(false))
                throw new BaseException(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION);
            log.info("삭제할 댓글 정보: " + comment);

            // 댓글 삭제 권한을 확인한다.
            if (!comment.getWriter().equals(blog)) { // 댓글 작성자가 아니라면
                throw new BaseException(BaseErrorCode.PERMISSION_EXCEPTION); // permission exception
            }

            // 댓글 좋아요 정보 삭제
            userCommentLikeRepository.deleteByComment(comment);

            // 댓글 삭제
            comment.setStatus(false);
            comment.resetCommentLike();

            comment.getPost().deleteComment();

            log.info("댓글이 삭제되었습니다.");

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
