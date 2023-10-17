package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.GetPostResDto;
import keapoint.onlog.post.dto.post.GetRecentPostResDto;
import keapoint.onlog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    /**
     * 모든 유효하고 공개된 게시글을 수정일자 내림차순으로 페이지네이션하여 반환한다.
     *
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 모든 유효하고 공개된 게시글의 페이지네이션 결과
     */
    @GetMapping("")
    public BaseResponse<Page<GetRecentPostResDto>> getPosts(Pageable pageable) {
        try {
            return new BaseResponse<>(postService.getRecentPosts(pageable));

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 게시글 조회
     * @param postId 게시글 식별자
     * @return 게시글 정보
     */
    @GetMapping("/{postId}")
    public BaseResponse<GetPostResDto> getPost(@PathVariable UUID postId) {
        try {
            return new BaseResponse<>(postService.getPost(postId));

        } catch (BaseException exception) {
            return new BaseResponse<>(exception);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
