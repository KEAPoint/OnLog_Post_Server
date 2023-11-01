package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
class BlogServiceIntegrationTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void 데이터_삭제() {
        blogRepository.deleteAll();
    }

    @Test
    void 블로그_생성_후_삭제_후_조회() throws BaseException {
        // given: 삭제된 블로그를
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");

        PostCreateBlogReqDto reqDto = PostCreateBlogReqDto.builder()
                .blogId(blogId) // 사용자 블로그 id
                .blogName("Hani Tech World") // 사용자 블로그 이름
                .blogNickname("hanitech") // 사용자 블로그 별명. 닉네임
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.") // 사용자 블로그 한 줄 소개
                .build();

        blogService.createBlog(reqDto);
        blogService.deleteBlog(blogId);

        // when: 조회하려고 하면
        // then: BLOG_NOT_FOUND_EXCEPTION 예외가 터져야 한다.
        BaseException thrownException = assertThrows(BaseException.class, () -> blogService.getProfile(blogId));

        assertEquals(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }


}