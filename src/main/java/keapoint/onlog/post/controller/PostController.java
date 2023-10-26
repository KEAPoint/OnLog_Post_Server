package keapoint.onlog.post.controller;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.*;
import keapoint.onlog.post.dto.topic.TopicDto;
import keapoint.onlog.post.entity.Topic;
import keapoint.onlog.post.repository.TopicRepository;
import keapoint.onlog.post.service.PostLikeService;
import keapoint.onlog.post.service.PostService;
import keapoint.onlog.post.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    private final TopicRepository topicRepository;

    private final PostLikeService postLikeService;

    private final JwtTokenProvider jwtTokenProvider;

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

    /**
     * 게시글 삭제
     *
     * @param token 사용자 access token
     * @param dto   삭제하고자 하는 게시글 식별자가 들어있는 객체
     * @return 게시글 삭제 성공 여부
     */
    @DeleteMapping("")
    public BaseResponse<DeletePostResDto> deleteComment(@RequestHeader("Authorization") String token,
                                                        @RequestBody DeletePostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postService.deletePost(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    /**
     * 게시글 주제 목록 조회
     *
     * @return 게시글 주제 목록
     */
    @GetMapping("/topics")
    public BaseResponse<List<TopicDto>> getTopicList() {
        try {
            List<Topic> topics = topicRepository.findAll();

            return new BaseResponse<>(topics.stream()
                    .map(topic -> TopicDto.builder()
                            .id(topic.getId())
                            .name(topic.getName())
                            .build())
                    .toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 게시글 좋아요 API
     *
     * @param postId 게시글 식별자
     * @param token  사용자 token
     * @return 게시글 좋아요 상태 변경 결과를 포함하는 응답 객체
     */
    @PostMapping("/{postId}/like")
    public BaseResponse<PostUpdateLikeResDto> likePost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        try {
            UUID userId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            PostUpdateLikeReqDto dto = new PostUpdateLikeReqDto(postId, true);
            return new BaseResponse<>(postLikeService.toggleLike(userId, dto)); // 좋아요 추가 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    /**
     * 게시글 좋아요 취소 API
     *
     * @param postId 게시글 식별자
     * @param token  사용자 token
     * @return 게시글 좋아요 상태 변경 결과를 포함하는 응답 객체
     */
    @DeleteMapping("/{postId}/like")
    public BaseResponse<PostUpdateLikeResDto> unlikePost(@PathVariable UUID postId, @RequestHeader("Authorization") String token) {
        try {
            UUID userId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            PostUpdateLikeReqDto dto = new PostUpdateLikeReqDto(postId, false);
            return new BaseResponse<>(postLikeService.toggleLike(userId, dto)); // 좋아요 제거 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

}
