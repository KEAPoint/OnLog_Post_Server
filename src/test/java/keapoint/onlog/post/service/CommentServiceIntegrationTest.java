package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.dto.comment.*;
import keapoint.onlog.post.dto.post.PostSummaryDto;
import keapoint.onlog.post.dto.post.PostWithRelatedPostsDto;
import keapoint.onlog.post.dto.post.PostWritePostReqDto;
import keapoint.onlog.post.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CommentServiceIntegrationTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private UserCommentLikeRepository userCommentLikeRepository;

    @BeforeEach
    void removeData() {
        userCommentLikeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("댓글 생성 후 삭제 후 조회")
    void test1() throws BaseException {
        // given: 댓글이 주어졌을 때
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(true)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 댓글 작성
        PostCreateCommentReqDto postCreateCommentReqDto = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("이것은 테스트 댓글입니다.")
                .parentCommentId(null)
                .build();
        CommentSummaryDto commentDto = commentService.createComment(blogId, postCreateCommentReqDto); // 댓글 생성
        UUID commentId = commentDto.getCommentId();

        // when: 댓글을 삭제하면
        DeleteCommentReqDto deleteReqDto = new DeleteCommentReqDto(commentId);
        commentService.deleteComment(blogId, deleteReqDto);

        // then: 댓글을 조회했을 때 조회가 되지 않아야 한다.
        // 게시글 조회 (댓글 조회 로직이 게시글 조회할 때만 하기 때문)
        PostWithRelatedPostsDto post = postService.getPost(blogId, postId);

        // 댓글 추출
        List<CommentDto> comments = post.getData().getComments();

        // 댓글이 있는지 확인
        boolean commentExists = comments.stream()
                .anyMatch(comment -> comment.getCommentId().equals(commentId));

        if (commentExists) { // 삭제한 댓글이 조회된다면
            fail("삭제된 댓글은 조회가 되면 안됩니다.");
        }
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("댓글 생성 후 삭제 후 수정")
    void test2() throws BaseException {
        // given: 댓글이 주어졌을 때
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(true)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 댓글 작성
        PostCreateCommentReqDto postCreateCommentReqDto = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("이것은 테스트 댓글입니다.")
                .parentCommentId(null)
                .build();
        CommentSummaryDto commentDto = commentService.createComment(blogId, postCreateCommentReqDto); // 댓글 생성
        UUID commentId = commentDto.getCommentId();

        // when: 댓글을 삭제하면
        DeleteCommentReqDto deleteReqDto = new DeleteCommentReqDto(commentId);
        commentService.deleteComment(blogId, deleteReqDto);

        // then: 댓글을 수정했을 때 COMMENT_NOT_FOUND_EXCEPTION이 발생해야 한다.
        PutUpdateCommentReqDto dto = PutUpdateCommentReqDto.builder()
                .commentId(commentId)
                .content("수정된 테스트 댓글입니다.")
                .build();

        BaseException thrownException = assertThrows(BaseException.class, () -> commentService.updateComment(blogId, dto));

        assertEquals(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("댓글 생성 후 삭제 후 삭제")
    void test3() throws BaseException {
        // given: 댓글이 주어졌을 때
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(true)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 댓글 작성
        PostCreateCommentReqDto postCreateCommentReqDto = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("이것은 테스트 댓글입니다.")
                .parentCommentId(null)
                .build();
        CommentSummaryDto commentDto = commentService.createComment(blogId, postCreateCommentReqDto); // 댓글 생성
        UUID commentId = commentDto.getCommentId();

        // when: 댓글을 삭제하면
        DeleteCommentReqDto deleteReqDto = new DeleteCommentReqDto(commentId);
        commentService.deleteComment(blogId, deleteReqDto);

        // then: 댓글을 삭제했을 때 COMMENT_NOT_FOUND_EXCEPTION이 발생해야 한다.
        DeleteCommentReqDto dto = new DeleteCommentReqDto(commentId);

        BaseException thrownException = assertThrows(BaseException.class, () -> commentService.deleteComment(blogId, dto));

        assertEquals(BaseErrorCode.COMMENT_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("좋아요가 달린 댓글 삭제")
    void test4() throws BaseException {
        // given: 댓글이 주어졌을 때
        // 블로그 생성
        UUID haniBlogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(haniBlogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        UUID wooseokBlogId = UUID.fromString("5a7a63d7-02c7-43a0-82ec-f2a80bd2eba4");
        PostCreateBlogReqDto postCreateWooseokBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(wooseokBlogId) // 사용자 블로그 식별자
                .blogName("Wooseok Tech World") // 사용자 블로그 이름
                .blogNickname("wooseoktech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Woo Seok Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateWooseokBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(haniBlogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(true)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(haniBlogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 댓글 작성
        PostCreateCommentReqDto postCreateCommentReqDto = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("이것은 테스트 댓글입니다.")
                .parentCommentId(null)
                .build();
        CommentSummaryDto commentDto = commentService.createComment(haniBlogId, postCreateCommentReqDto); // 댓글 생성
        UUID commentId = commentDto.getCommentId();

        // 댓글 좋아요
        commentLikeService.toggleLike(wooseokBlogId, commentId, true);

        // when: 댓글을 삭제하면
        DeleteCommentReqDto deleteReqDto = new DeleteCommentReqDto(commentId);
        commentService.deleteComment(haniBlogId, deleteReqDto);

        // then: 댓글을 조회했을 때 조회가 되지 않아야 한다.
        // 게시글 조회 (댓글 조회 로직이 게시글 조회할 때만 하기 때문)
        PostWithRelatedPostsDto post = postService.getPost(haniBlogId, postId);

        // 댓글 추출
        List<CommentDto> comments = post.getData().getComments();

        // 댓글이 있는지 확인
        boolean commentExists = comments.stream()
                .anyMatch(comment -> comment.getCommentId().equals(commentId));

        if (commentExists) { // 삭제한 댓글이 조회된다면
            fail("삭제된 댓글은 조회가 되면 안됩니다.");
        }
    }


}
