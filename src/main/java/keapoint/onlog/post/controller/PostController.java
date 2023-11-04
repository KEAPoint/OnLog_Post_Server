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
import io.swagger.v3.oas.annotations.Operation;

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

    @Operation(summary = "(카드) 최근 게시글 조회", description = "주제나 해시태그에 따른 게시글을 조회합니다.")
    @GetMapping("")
    public BaseResponse<Page<PostDto>> getPosts(
            @RequestParam(value = "topic", required = false) String topicName,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            Pageable pageable
    ) {
        try {
            return new BaseResponse<>(postService.getRecentPosts(topicName, hashtag, categoryId, pageable));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "특정 게시글 조회", description = "ID에 따른 특정 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public BaseResponse<PostDto> getPost(@PathVariable UUID postId) {
        try {
            return new BaseResponse<>(postService.getPost(postId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "비공개 게시글 조회", description = "나의 비공개 게시글을 조회합니다.")
    @GetMapping("/private")
    public BaseResponse<Page<PostDto>> getPrivatePosts(@RequestHeader("Authorization") String token, Pageable pageable) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postService.getPrivatePosts(blogId, pageable));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @PostMapping("")
    public BaseResponse<PostDto> writePost(@RequestHeader("Authorization") String token,
                                           @RequestBody PostWritePostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postService.writePost(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("")
    public BaseResponse<PostDto> modifyPost(@RequestHeader("Authorization") String token,
                                            @RequestBody PutModifyPostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(postService.modifyPost(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("")
    public BaseResponse<PostDto> deletePost(@RequestHeader("Authorization") String token,
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

    @Operation(summary = "게시글 주제 목록 조회", description = "게시글 주제 목록을 조회합니다.")
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

    @Operation(summary = "게시글 좋아요", description = "사용자가 특정 게시글에 좋아요를 남깁니다.")
    @PostMapping("/like")
    public BaseResponse<PostPostLikeResDto> likePost(@RequestHeader("Authorization") String token,
                                                     @RequestBody PostPostLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new PostPostLikeResDto(postLikeService.toggleLike(blogId, dto.getPostId(), true))); // 좋아요 추가 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 좋아요 취소", description = "사용자가 특정 게시물에 남긴 좋아요를 취소합니다.")
    @DeleteMapping("/like")
    public BaseResponse<DeletePostLikeResDto> unlikePost(@RequestHeader("Authorization") String token,
                                                         @RequestBody DeletePostLikeReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return new BaseResponse<>(new DeletePostLikeResDto(postLikeService.toggleLike(blogId, dto.getPostId(), false))); // 좋아요 제거 처리 서비스 호출

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

}
