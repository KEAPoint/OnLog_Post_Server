package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
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
class BlogServiceIntegrationTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void removeData() {
        blogRepository.deleteAll();
    }

    @Test
    @DisplayName("블로그 생성 후 삭제 후 조회")
    void test1() throws BaseException {
        // given: 삭제된 블로그를
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");

        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 식별자
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .blogProfileImg("http://haniTechProfileImageUrl.com") // 사용자 프로필 이미지
                .build();

        blogService.createBlog(reqDto);
        blogService.deleteBlog(blogId);

        // when: 조회하려고 하면
        // then: BLOG_NOT_FOUND_EXCEPTION 예외가 터져야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.getProfile(blogId));

        assertEquals(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }


}