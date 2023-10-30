package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.blog.*;
import keapoint.onlog.post.service.BlogService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final JwtTokenProvider jwtTokenProvider;

    private final BlogService blogService;

    @Operation(summary = "블로그 생성", description = "새로운 블로그를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description="블로그 생성 성공", content=@Content(schema=@Schema(implementation=BaseResponse.class))),
    })
    @PostMapping("")
    public BaseResponse<PostCreateBlogResDto> createBlog(@RequestBody PostCreateBlogReqDto data) {
        try {
            return new BaseResponse<>(blogService.createBlog(data));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "프로필 조회", description = "나의 블로그 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description="프로필 조회 성공", content=@Content(schema=@Schema(implementation=BaseResponse.class))),
    })
    @GetMapping("/profile")
    public BaseResponse<BlogDto> getMyProfile(@RequestHeader("Authorization") String token) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(blogService.getProfile(blogId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "팔로우 설정", description = "특정 블로그를 팔로우합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description="팔로우 설정 성공", content=@Content(schema=@Schema(implementation=BaseResponse.class)))
    })
    @PostMapping("/follow")
    public BaseResponse<PostFollowResDto> follow(@RequestHeader("Authorization") String token,
                                                 @RequestBody PostFollowReqDto data) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new PostFollowResDto(blogService.toggleFollow(blogId, data.getTargetBlogId(), true)));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "팔로우 해제", description = "특정 블로그 팔로우를 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description="팔로우 해제 성공", content=@Content(schema=@Schema(implementation=BaseResponse.class))),
    })
    @DeleteMapping("/follow")
    public BaseResponse<DeleteFollowResDto> unFollow(@RequestHeader("Authorization") String token,
                                                     @RequestBody DeleteFollowReqDto data) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new DeleteFollowResDto(blogService.toggleFollow(blogId, data.getTargetBlogId(), false)));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }
}
