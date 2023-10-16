package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.post.PostCreateBlogResDto;
import keapoint.onlog.post.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;

    @PostMapping("")
    public BaseResponse<PostCreateBlogResDto> createBlog(@RequestBody PostCreateBlogReqDto data) {
        try {
            return new BaseResponse<>(blogService.createBlog(data));

        } catch (BaseException exception) {
            return new BaseResponse<>(exception);
        }
    }
}
