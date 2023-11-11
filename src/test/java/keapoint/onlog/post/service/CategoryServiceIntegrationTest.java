package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.config.TestSecurityConfig;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CategoryServiceIntegrationTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void removeData() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 삭제")
    void test1() throws BaseException {
        // given: 생성된 카테고리를
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

        // when: 삭제하면
        categoryService.deleteCategory(blogId, new DeleteCategoryReqDto(categoryId));

        // then: 조회가 되면 안된다
        assertEquals (List.of(), categoryService.getCategories(blogId));
    }

}