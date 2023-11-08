package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
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

import java.util.List;
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

    @BeforeEach
    void removeData() {
        userPostLikeRepository.deleteAll();
        categoryRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("비공개 게시글 조회")
    void test1() throws BaseException {
        // given: 비공개 게시글을 작성했을 때
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // when: 비공개 게시글을 조회하면
        PostWithRelatedPostsDto post = postService.getPost(blogId, postId);

        // then: 정상적으로 조회가 되어야 한다
        assertEquals(post.getData().getPostId(), postId);
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("비공개 게시글을 타인이 조회하는 경우")
    void test2() throws BaseException {
        // given: 비공개 게시글을 작성했을 때
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
                .isPublic(false)
                .categoryId(categoryId)
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
                .isPublic(false)
                .categoryId(categoryId)
                .hashtagList(List.of("테스트"))
                .topicId(1L)
                .build();
        PostSummaryDto postSummaryDto = postService.writePost(haniBlogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 좋아요 설정
        postLikeService.toggleLike(wooseokBlogId, postId, true);

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
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 삭제
        postService.deletePost(blogId, new DeletePostReqDto(postId));

        // when: 게시글을 조회했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postService.getPost(blogId, postId));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 수정")
    void test5() throws BaseException {
        // given: 삭제된 게시글은
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 삭제
        postService.deletePost(blogId, new DeletePostReqDto(postId));

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

        BaseException thrownException = assertThrows(BaseException.class, () -> postService.modifyPost(blogId, data));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 삭제")
    void test6() throws BaseException {
        // given: 삭제된 게시글은
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 삭제
        postService.deletePost(blogId, new DeletePostReqDto(postId));

        // when: 게시글을 삭제했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postService.deletePost(blogId, new DeletePostReqDto(postId)));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("삭제된 게시글 좋아요")
    void test7() throws BaseException {
        // given: 삭제된 게시글은
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // 게시글 삭제
        postService.deletePost(blogId, new DeletePostReqDto(postId));

        // when: 게시글을 좋아요 했을 때
        // then: POST_NOT_FOUND_EXCEPTION이 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> postLikeService.toggleLike(blogId, postId, true));

        assertEquals(BaseErrorCode.POST_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @Transactional // @Transactional을 사용하여 Hibernate 세션이 메소드 호출 동안 열려 있도록 설정
    @DisplayName("카테고리 삭제로 인한 게시글 카테고리 변경")
    void test8() throws BaseException {
        // given: 게시글에 카테고리가 설정되어 있는데
        // 블로그 생성
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto postCreateHaniBlogReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId)
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg")
                .build();
        blogService.createBlog(postCreateHaniBlogReqDto);

        // 카테고리 생성
        CategoryDto categoryDto = categoryService.createCategory(blogId, new PostCreateCategoryReqDto("TestCategory"));
        Long categoryId = categoryDto.getId();

        // 게시글 작성
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
        PostSummaryDto postSummaryDto = postService.writePost(blogId, postWritePostReqDto);
        UUID postId = postSummaryDto.getPostId();

        // when: 카테고리가 삭제되면
        categoryService.deleteCategory(blogId, new DeleteCategoryReqDto(categoryId));

        // then: null로 변해야한다.
        Post post = postRepository.findById(postId).get();
        assertNull(post.getCategory());
    }


}