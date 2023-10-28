package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.comment.*;
import keapoint.onlog.post.service.CommentService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/comments")
public class CommentController {

    private final JwtTokenProvider jwtTokenProvider;

    private final CommentService commentService;

    @PostMapping("")
    public BaseResponse<CommentDto> createComment(@RequestHeader("Authorization") String token,
                                                  @RequestBody PostCreateCommentReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(commentService.createComment(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @PutMapping("")
    public BaseResponse<CommentDto> updateComment(@RequestHeader("Authorization") String token,
                                                  @RequestBody PutUpdateCommentReqDto dto) {

        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(commentService.updateComment(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("")
    public BaseResponse<DeleteCommentResDto> deleteComment(@RequestHeader("Authorization") String token,
                                                           @RequestBody DeleteCommentReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(commentService.deleteComment(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @PostMapping("/like")
    public BaseResponse<PostCommentLikeResDto> cancelLikeComment(@RequestHeader("Authorization") String token,
                                                                 @RequestBody PostCommentLikeReqDto dto) {

        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new PostCommentLikeResDto(commentService.toggleLike(blogId, dto.getCommentId(), true)));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }

    }

    @DeleteMapping("/like")
    public BaseResponse<DeleteCommentLikeResDto> cancelLikeComment(@RequestHeader("Authorization") String token,
                                                                   @RequestBody DeleteCommentLikeReqDto dto) {

        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new DeleteCommentLikeResDto(commentService.toggleLike(blogId, dto.getCommentId(), false)));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }

    }

}
