package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.GetPostResDto;
import keapoint.onlog.post.dto.post.GetPostListResDto;
import keapoint.onlog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    /**
     * 최신 게시글 조회
     *
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 최신 게시글 정보
     */
    @GetMapping("")
    public BaseResponse<Page<GetPostListResDto>> getPosts(
            @RequestParam(value = "topic", required = false) String topicName,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            Pageable pageable
    ) {
        try {
            if (topicName != null && !topicName.isEmpty()) {
                return new BaseResponse<>(postService.getRecentPostsByTopicName(topicName, pageable));
            } else if (hashtag != null && !hashtag.isEmpty()) {
                return new BaseResponse<>(postService.getPostsByHashtag(hashtag, pageable));
            } else {
                return new BaseResponse<>(postService.getRecentPosts(pageable));
            }

        } catch (BaseException exception) {
            return new BaseResponse<>(exception);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 게시글 조회
     *
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
