package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.comment.CommentDto;
import keapoint.onlog.post.dto.comment.PostCreateCommentReqDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CommentRepository;
import keapoint.onlog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final BlogRepository blogRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public CommentDto createComment(UUID blogId, PostCreateCommentReqDto data) throws BaseException {
        try {
            Blog writer = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            Post post = postRepository.findById(data.getPostId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.POST_NOT_FOUND_EXCEPTION));

            long ref; // 그룹
            long refOrder; // 그룹 순서
            long step; // 댓글의 계층
            UUID parentNum = data.getParentNum(); // 부모댓글의 ID
            long answerNum = 0L; // 해당댓글의 자식댓글의 수. 댓글이 작성된 경우이므로 0

            if (parentNum != null) { // 부모 댓글이 있는 경우 (대댓글)
                Comment parentComment = commentRepository.findById(data.getParentNum())
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
                    .post(post)
                    .writer(writer)
                    .build();

            // 댓글 저장
            commentRepository.save(comment);
            comment.addComment(post);

            return comment.toEntity();

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
}
