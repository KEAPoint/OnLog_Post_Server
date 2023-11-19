package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.dto.comment.DeleteCommentReqDto;
import keapoint.onlog.post.dto.comment.PostCreateCommentReqDto;
import keapoint.onlog.post.dto.post.*;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.entity.UserPostLike;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CategoryRepository;
import keapoint.onlog.post.repository.PostRepository;
import keapoint.onlog.post.repository.UserPostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class PostServiceIntegrationTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private UserPostLikeRepository userPostLikeRepository;

    @Autowired
    private CommentService commentService;


    private final UUID haniBlogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
    private final List<Long> haniCategoryList = new ArrayList<>();

    private final UUID wooseokBlogId = UUID.fromString("5a7a63d7-02c7-43a0-82ec-f2a80bd2eba4");

    @BeforeEach
    void setUp() throws Exception {
        userPostLikeRepository.deleteAll();
        categoryRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();

        // 하니 블로그 및 카테고리 생성
        createBlog(haniBlogId, "Hani Tech World", "hanitech", "Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.");
        for (int i = 0; i < 3; i++) {
            haniCategoryList.add(createCategory("test" + i));
        }

        // 우석 블로그 생성
        createBlog(UUID.fromString("5a7a63d7-02c7-43a0-82ec-f2a80bd2eba4"), "Wooseok Tech World", "wooseoktech", "Woo Seok Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.");
    }

    void createBlog(UUID blogId, String blogName, String blogNickname, String blogIntro) throws Exception {
        PostCreateBlogReqDto postCreateBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName(blogName)
                .blogNickname(blogNickname)
                .blogIntro(blogIntro)
                .blogProfileImg(null)
                .build();

        blogService.createBlog(postCreateBlogReqDto);
    }

    Long createCategory(String name) throws Exception {
        CategoryDto categoryDto = categoryService.createCategory(haniBlogId, new PostCreateCategoryReqDto(name));
        return categoryDto.getId();
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("비공개 게시글 조회")
    void test1() throws BaseException {
        // given: 비공개 게시글을 작성했을 때
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // when: 비공개 게시글을 조회하면
        PostWithRelatedPostsDto post = postService.getPost(haniBlogId, postId);

        // then: 정상적으로 조회가 되어야 한다
        assertEquals(post.getData().getPostId(), postId);
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("비공개 게시글을 타인이 조회하는 경우")
    void test2() throws BaseException {
        // given: 비공개 게시글을 작성했을 때
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(haniBlogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // when: 내가 아닌 다른 사용자가 비공개 게시글을 조회하면
        // then: ACCESS_DENIED_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postService.getPost(wooseokBlogId, postId));

        assertEquals(BaseErrorCode.ACCESS_DENIED_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 정보 확인")
    void test3() throws BaseException {
        // given: 삭제된 게시글은
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 게시글 좋아요 설정
        postLikeService.toggleLike(wooseokBlogId, postId);

        // 게시글 삭제
        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 좋아요 정보를 조회했을 때
        Post post = postRepository.findById(postId).get();
        List<UserPostLike> likeInformation = userPostLikeRepository.findByPost(post).get();

        // then: 없어야 한다.
        assertEquals(0, likeInformation.size());
        assertEquals(0, post.getLikesCount());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 조회")
    void test4() throws BaseException {
        // given: 삭제된 게시글은
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 게시글 삭제
        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 게시글을 조회했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postService.getPost(haniBlogId, postId));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 수정")
    void test5() throws BaseException {
        // given: 삭제된 게시글은
        Random rand = new Random();
        Long categoryId = haniCategoryList.get(rand.nextInt(haniCategoryList.size()));

        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 게시글 삭제
        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 게시글을 수정했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        PutModifyPostReqDto data = PutModifyPostReqDto.builder()
                .postId(postId)
                .title("수정된 제목")
                .content("삭제된 댓글을 수정하면 어떻게 될까요?")
                .summary("오류가 터져야해요")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(true)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();

        BaseException thrownException = assertThrows(BaseException.class, () -> postService.modifyPost(haniBlogId, data));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 삭제")
    void test6() throws BaseException {
        // given: 삭제된 게시글은
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(haniBlogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 삭제
        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 게시글을 삭제했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postService.deletePost(haniBlogId, new DeletePostReqDto(postId)));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 좋아요")
    void test7() throws BaseException {
        // given: 삭제된 게시글은
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 게시글 삭제
        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 게시글을 좋아요 했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postLikeService.toggleLike(haniBlogId, postId));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("카테고리 삭제로 인한 게시글 카테고리 변경")
    void test8() throws BaseException {
        // given: 게시글에 카테고리가 설정되어 있는데
        Random rand = new Random();
        Long categoryId = haniCategoryList.get(rand.nextInt(haniCategoryList.size()));

        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // when: 카테고리가 삭제되면
        categoryService.deleteCategory(haniBlogId, new DeleteCategoryReqDto(categoryId));

        // then: null로 변해야한다.
        Post post = postRepository.findById(postId).get();
        assertNull(post.getCategory());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 댓글 작성")
    void test9() throws BaseException {
        // given: 삭제된 게시글에
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        postService.deletePost(haniBlogId, new DeletePostReqDto(postId));

        // when: 댓글이 작성되면
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        PostCreateCommentReqDto comment = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("test comment")
                .parentCommentId(null)
                .build();

        BaseException thrownException = assertThrows(BaseException.class, () -> commentService.createComment(wooseokBlogId, comment));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    // 게시글 댓글 갯수 확인
    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("게시글 댓글 개수 확인")
    void test10() throws BaseException {
        // given: 게시글에 댓글을 작성하면
        // 게시글 작성
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 댓글 작성
        PostCreateCommentReqDto comment = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("test comment")
                .parentCommentId(null)
                .build();
        commentService.createComment(wooseokBlogId, comment);

        // when: 게시글을 조회했을 때
        PostWithRelatedPostsDto post = postService.getPost(haniBlogId, postId);

        // then: 댓글이 1개가 나와야 한다
        assertEquals(1, post.getData().getCommentsCounts());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("게시글 댓글 개수 확인")
    void test11() throws BaseException {
        // given: 게시글에 댓글을 작성하고 해당 댓글을 삭제했을 때
        // 게시글 작성
        Random rand = new Random();
        PostWritePostReqDto postWritePostReqDto = PostWritePostReqDto.builder()
                .title("테스트 제목")
                .content("이것은 테스트 게시글입니다.")
                .summary("테스트")
                .thumbnailLink("https://cdn-lostark.game.onstove.com/uploadfiles/user/2021/04/01/637528990397262868.png")
                .isPublic(false)
                .categoryId(haniCategoryList.get(rand.nextInt(haniCategoryList.size())))
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        UUID postId = postService.writePost(haniBlogId, postWritePostReqDto).getPostId();

        // 댓글 작성
        PostCreateCommentReqDto comment = PostCreateCommentReqDto.builder()
                .postId(postId)
                .content("test comment")
                .parentCommentId(null)
                .build();
        UUID commentId = commentService.createComment(wooseokBlogId, comment).getCommentId();

        // 댓글 삭제
        commentService.deleteComment(wooseokBlogId, new DeleteCommentReqDto(commentId));

        // when: 게시글을 조회했을 때
        PostWithRelatedPostsDto post = postService.getPost(haniBlogId, postId);

        // then: 댓글이 1개가 나와야 한다
        assertEquals(0, post.getData().getCommentsCounts());
    }

}