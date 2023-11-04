package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.like.DeletePostLikeReqDto;
import keapoint.onlog.post.dto.post.like.PostLikeDto;
import keapoint.onlog.post.dto.post.like.PostPostLikeReqDto;
import keapoint.onlog.post.service.PostLikeService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@Tag(name = "Post")
@RequiredArgsConstructor
@RequestMapping("/posts/like")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "게시글 좋아요", description = "사용자가 특정 게시글에 좋아요를 남깁니다.")
    @PostMapping("")
    public BaseResponse<PostLikeDto> likePost(@RequestHeader("Authorization") String token,
                                              @RequestBody PostPostLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postLikeService.toggleLike(blogId, dto.getPostId(), true)); // 좋아요 추가 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 좋아요 취소", description = "사용자가 특정 게시물에 남긴 좋아요를 취소합니다.")
    @DeleteMapping("")
    public BaseResponse<PostLikeDto> unlikePost(@RequestHeader("Authorization") String token,
                                                @RequestBody DeletePostLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postLikeService.toggleLike(blogId, dto.getPostId(), false)); // 좋아요 제거 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

}
