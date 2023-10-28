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

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final JwtTokenProvider jwtTokenProvider;

    private final BlogService blogService;

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
