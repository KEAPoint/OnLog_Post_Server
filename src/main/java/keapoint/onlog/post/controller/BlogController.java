package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.UUID;


@Slf4j
@RestController
@Tag(name = "Blog")
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final JwtTokenProvider jwtTokenProvider;

    private final BlogService blogService;

    @Operation(summary = "블로그 생성", description = "새로운 블로그를 생성합니다.", hidden = true)
    @PostMapping("")
    public BaseResponse<BlogDto> createBlog(@RequestBody PostCreateBlogReqDto data) {
        try {
            return BaseResponse.onCreate(blogService.createBlog(data));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "블로그 수정", description = "사용자 블로그 정보를 수정합니다.")
    @PutMapping("")
    public BaseResponse<BlogDto> updateBlog(@RequestHeader("Authorization") String token,
                                            @RequestBody PutUpdateBlogReqDto data) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(blogService.updateBlog(blogId, data));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "블로그 탈퇴", description = "사용자의 블로그를 탈퇴합니다.")
    @DeleteMapping("")
    public BaseResponse<BlogDto> deleteBlog(@RequestHeader("Authorization") String token) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 블로그 ID 추출 후 UUID로 변환
            return BaseResponse.onSuccess(blogService.deleteBlog(blogId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "블로그 조회", description = "블로그를 조회합니다.")
    @GetMapping("")
    public BaseResponse<BlogProfileDto> getMyProfile(@RequestParam("blog_id") UUID blogId) {
        try {
            return BaseResponse.onSuccess(blogService.getProfile(blogId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }
}
