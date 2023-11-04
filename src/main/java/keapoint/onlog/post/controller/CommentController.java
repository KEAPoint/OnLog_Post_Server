package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.comment.*;
import keapoint.onlog.post.service.CommentService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.UUID;

@Slf4j
@RestController
@Tag(name = "Comment")
@RequiredArgsConstructor
@RequestMapping("/post/comments")
public class CommentController {

    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
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

    @Operation(summary = "댓글 수정", description = "사용자가 작성한 댓글의 내용을 수정합니다.")
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

    @Operation(summary = "댓글 삭제", description = "사용자가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("")
    public BaseResponse<CommentDto> deleteComment(@RequestHeader("Authorization") String token,
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

}
