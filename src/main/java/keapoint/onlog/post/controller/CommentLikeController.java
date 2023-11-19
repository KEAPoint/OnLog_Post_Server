package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.comment.like.CommentLikeDto;
import keapoint.onlog.post.dto.comment.like.DeleteCommentLikeReqDto;
import keapoint.onlog.post.dto.comment.like.PostCommentLikeReqDto;
import keapoint.onlog.post.service.CommentLikeService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@Tag(name = "Comment")
@RequiredArgsConstructor
@RequestMapping("/post/comments/like")
public class CommentLikeController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CommentLikeService commentLikeService;

    @Operation(summary = "댓글 좋아요", description = "사용자가 특정 댓글에 좋아요를 남깁니다.")
    @PostMapping("")
    public BaseResponse<CommentLikeDto> cancelLikeComment(@RequestHeader("Authorization") String token,
                                                          @RequestBody PostCommentLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(commentLikeService.toggleLike(blogId, dto.getCommentId()));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }

    }

    @Operation(summary = "댓글 좋아요 취소", description = "사용자가 특정 댓글에 남긴 좋아요를 취소합니다.")
    @DeleteMapping("")
    public BaseResponse<CommentLikeDto> cancelLikeComment(@RequestHeader("Authorization") String token,
                                                          @RequestBody DeleteCommentLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onSuccess(commentLikeService.toggleLike(blogId, dto.getCommentId()));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }

    }

}
