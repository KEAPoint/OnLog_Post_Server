package keapoint.onlog.post.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.base.BaseResponse;
import keapoint.onlog.post.dto.post.*;
import keapoint.onlog.post.dto.topic.TopicDto;
import keapoint.onlog.post.entity.Topic;
import keapoint.onlog.post.repository.TopicRepository;
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
@Tag(name = "Post")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final TopicRepository topicRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "(카드) 최근 게시글 조회", description = "조건에 따른 게시글을 조회합니다.")
    @GetMapping("")
    public BaseResponse<Page<PostSummaryDto>> getPosts(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "topic", required = false) String topicName,
            @RequestParam(value = "hashtag", required = false) String hashtag,
            @RequestParam(value = "blog_id", required = false) UUID blogId,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "is_public", required = false) Boolean isPublic,
            Pageable pageable
    ) {
        try {
            UUID myBlogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onSuccess(postService.getRecentPosts(myBlogId, topicName, hashtag, blogId, categoryId, isPublic, pageable));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "특정 게시글 조회", description = "ID에 따른 특정 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public BaseResponse<PostWithRelatedPostsDto> getPost(@RequestHeader("Authorization") String token,
                                                         @PathVariable UUID postId) {
        try {
            UUID myBlogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onSuccess(postService.getPost(myBlogId, postId));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Deprecated
    @Operation(summary = "비공개 게시글 조회", description = "나의 비공개 게시글을 조회합니다.")
    @GetMapping("/private")
    public BaseResponse<Page<PostSummaryDto>> getPrivatePosts(@RequestHeader("Authorization") String token, Pageable pageable) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onSuccess(postService.getPrivatePosts(blogId, pageable));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @PostMapping("")
    public BaseResponse<PostSummaryDto> writePost(@RequestHeader("Authorization") String token,
                                                  @RequestBody PostWritePostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(postService.writePost(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("")
    public BaseResponse<PostSummaryDto> modifyPost(@RequestHeader("Authorization") String token,
                                                   @RequestBody PutModifyPostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            return BaseResponse.onCreate(postService.modifyPost(blogId, dto));

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("")
    public BaseResponse<PostSummaryDto> deletePost(@RequestHeader("Authorization") String token,
                                                   @RequestBody DeletePostReqDto dto) {
        try {
            UUID blogId = UUID.fromString(jwtTokenProvider.extractIdx(token)); // JWT 토큰에서 사용자 ID 추출 후 UUID로 변환
            postService.deletePost(blogId, dto);
            return BaseResponse.onSuccess(null);

        } catch (BaseException e) {
            return new BaseResponse<>(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(new BaseException(BaseErrorCode.UNEXPECTED_ERROR));
        }
    }

    @Deprecated
    @Operation(summary = "게시글 주제 목록 조회", description = "게시글 주제 목록을 조회합니다.")
    @GetMapping("/topics")
    public BaseResponse<List<TopicDto>> getTopicList() {
        try {
            List<Topic> topics = topicRepository.findAll();

            return BaseResponse.onSuccess(topics.stream()
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
}
