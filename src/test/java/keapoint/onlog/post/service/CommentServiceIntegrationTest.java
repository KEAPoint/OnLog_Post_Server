package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.dto.comment.*;
import keapoint.onlog.post.dto.post.PostSummaryDto;
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

    @BeforeEach
    void removeData() {
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
                .blogProfileImg("http://haniTechProfileImageUrl.com")
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
        CommentDto commentDto = commentService.createComment(blogId, postCreateCommentReqDto); // 댓글 생성
        UUID commentId = commentDto.getCommentId();

        // when: 댓글을 삭제하면
        DeleteCommentReqDto deleteReqDto = new DeleteCommentReqDto(commentId);
        commentService.deleteComment(blogId, deleteReqDto);

        // then: 댓글을 조회했을 때 조회가 되지 않아야 한다.
        assertFalse(commentRepository.findById(commentId).isPresent());
    }
}
