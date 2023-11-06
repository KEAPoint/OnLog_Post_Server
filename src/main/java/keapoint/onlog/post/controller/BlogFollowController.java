package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.blog.follow.BlogFollowDto;
import keapoint.onlog.post.dto.blog.follow.DeleteFollowReqDto;
import keapoint.onlog.post.dto.blog.follow.PostFollowReqDto;
import keapoint.onlog.post.service.BlogFollowService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@Tag(name = "Blog")
@RequiredArgsConstructor
@RequestMapping("/blog/follow")
public class BlogFollowController {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlogFollowService blogFollowService;

    @Operation(summary = "팔로우 조회", description = "내가 팔로우 하고 있는 블로그를 조회합니다.", parameters = {
            @Parameter(name = "Authorization", description = "사용자 토큰", required = true, schema = @Schema(type = "string"))
    })
    @GetMapping("")
    public BaseResponse<List<BlogFollowDto>> follow(@RequestHeader("Authorization") String token) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(blogFollowService.getFollowers(blogId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "팔로우 설정", description = "특정 블로그를 팔로우합니다.", parameters = {
            @Parameter(name = "Authorization", description = "사용자 토큰", required = true, schema = @Schema(type = "string"))
    })
    @PostMapping("")
    public BaseResponse<BlogFollowDto> follow(@RequestHeader("Authorization") String token,
                                              @RequestBody PostFollowReqDto data) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(blogFollowService.toggleFollow(blogId, data.getTargetBlogId(), true));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "팔로우 해제", description = "특정 블로그 팔로우를 해제합니다.", parameters = {
            @Parameter(name = "Authorization", description = "사용자 토큰", required = true, schema = @Schema(type = "string"))
    })
    @DeleteMapping("")
    public BaseResponse<BlogFollowDto> unFollow(@RequestHeader("Authorization") String token,
                                                @RequestBody DeleteFollowReqDto data) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(blogFollowService.toggleFollow(blogId, data.getTargetBlogId(), false));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }
}