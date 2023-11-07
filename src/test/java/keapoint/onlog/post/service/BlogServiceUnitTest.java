package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class BlogServiceUnitTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void removeData() {
        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("블로그 생성")
    void test1() throws BaseException {
        // given: 새로운 블로그 생성이 들어왔을 때
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://haniTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        // when: 별 이상이 없는 경우 (중복된 식별자가 사용되지 않는 경우, 닉네임 중복이 없는 경우)
        blogService.createBlog(reqDto);

        // then: 블로그가 생성되어야 한다.
        Blog findBlog = blogRepository.findByBlogNickname("hanitech").get();

        // verify: 블로그 생성 요청 식별자와 생성된 블로그의 식별자가 같은지 검증
        assertEquals(blogId, findBlog.getBlogId());
    }

    @Test
    @DisplayName("블로그 생성")
    void test2() throws BaseException {
        // given: 새로운 블로그 생성이 들어왔을 때
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://haniTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        blogService.createBlog(reqDto);

        UUID anotherBlogId = UUID.fromString("5a7a63d7-02c7-43a0-82ec-f2a80bd2eba4");
        PostCreateBlogReqDto newReqDto = PostCreateBlogReqDto.builder()
                .blogId(anotherBlogId) // 사용자 블로그 식별자
                .blogName("Wooseok Tech World") // 사용자 블로그 이름
                .blogNickname("wooseoktech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Woo Seok Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://wooseokTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        // when: 별 이상이 없는 경우 (중복된 식별자가 사용되지 않는 경우, 닉네임 중복이 없는 경우)
        blogService.createBlog(newReqDto);

        // then: 블로그가 생성되어야 한다.
        Blog findBlog = blogRepository.findByBlogNickname("wooseoktech").get();

        // verify: 블로그 생성 요청 식별자와 생성된 블로그의 식별자가 같은지 검증
        assertEquals(anotherBlogId, findBlog.getBlogId());
    }

    @Test
    @DisplayName("블로그 생성 실패 by 식별자 중복")
    void test3() throws BaseException {
        // given: 사전에 있는 블로그 식별자로
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://haniTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        blogService.createBlog(reqDto);

        // when: 블로그를 생성하려고 하면
        PostCreateBlogReqDto newReqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Wooseok Tech World") // 사용자 블로그 이름
                .blogNickname("wooseoktech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Woo Seok Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://wooseokTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        // then: ALREADY_BLOG_EXISTS_EXCEPTION 예외가 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.createBlog(newReqDto));

        assertEquals(BaseErrorCode.ALREADY_BLOG_EXISTS_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @DisplayName("블로그 생성 실패 by 블로그 닉네임 중복")
    void test4() throws BaseException {
        // given: 사전에 있는 블로그 닉네임으로
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");
        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://haniTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        blogService.createBlog(reqDto);

        // when: 블로그를 생성하려고 하면
        UUID anotherBlogId = UUID.fromString("5a7a63d7-02c7-43a0-82ec-f2a80bd2eba4");
        PostCreateBlogReqDto newReqDto = PostCreateBlogReqDto.builder()
                .blogId(anotherBlogId) // 사용자 블로그 식별자
                .blogName("Wooseok Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Woo Seok Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://wooseokTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        // then: ALREADY_BLOG_NICKNAME_EXISTS_EXCEPTION 예외가 발생해야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.createBlog(newReqDto));

        assertEquals(BaseErrorCode.ALREADY_BLOG_NICKNAME_EXISTS_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @DisplayName("블로그 탈퇴 실패 by 존재하지 않은 블로그 탈퇴")
    void test5() {
        // given: 사용자가 블로그 탈퇴 요청을 했을 때
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");

        // when: 존재하지 않는 블로그를 탈퇴하고자 한다면
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.deleteBlog(blogId));

        // then: BLOG_NOT_FOUND_EXCEPTION 예외가 터져야 한다.
        assertEquals(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }

    @Test
    @DisplayName("블로그 프로필 조회 실패 by 존재하지 않는 블로그 조회")
    void test6() {
        // given: 사용자가 블로그 프로필 조회 요청을 했을 떄
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");

        // when: 존재하지 않는 블로그를 조회하고자 한다면
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.getProfile(blogId));

        // then: BLOG_NOT_FOUND_EXCEPTION 예외가 터져야 한다.
        assertEquals(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }
}