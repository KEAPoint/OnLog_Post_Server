package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.blog.BlogDto;
import keapoint.onlog.post.dto.blog.PostCreateBlogReqDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Category;
import keapoint.onlog.post.repository.CategoryRepository;
import keapoint.onlog.post.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @BeforeEach
    void 데이터_삭제() {
        categoryRepository.deleteAll();
        blogRepository.deleteAll();
    }

    @Test
    void 카테고리_생성_후_삭제_후_조회() throws BaseException {

        // blogId 선언과 초기화
        UUID blogId = UUID.fromString("48f99c85-ed6b-46c2-8f47-66f9f67040bc");

        // 블로그 생성
        PostCreateBlogReqDto blogReqDto = PostCreateBlogReqDto.builder()
                .blogName("Hani Tech World")
                .blogNickname("hanitech")
                .blogIntro("Hani Tech World는 최신 기술 정보와 실용적인 IT 팁을 제공하는 블로그입니다.")
                .blogProfileImg("http://haniTechProfileImageUrl.com")
                .build();
        BlogDto blog = blogService.createBlog(blogId, blogReqDto);

        // 카테고리 생성
        PostCreateCategoryReqDto categoryReqDto = new PostCreateCategoryReqDto();
        categoryReqDto.setName("Tech");
        CategoryDto createdCategory = categoryService.createCategory(blogId, categoryReqDto);

        // 카테고리 삭제
        DeleteCategoryReqDto deleteReqDto = new DeleteCategoryReqDto();
        deleteReqDto.setId(createdCategory.getId());
        categoryService.deleteCategory(blogId, deleteReqDto);

        // 삭제된 카테고리 조회 시도
        BaseException thrownException = assertThrows(BaseException.class, () -> categoryService.findCategoryById(blogId,createdCategory.getId()));
        assertEquals(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION, thrownException.getErrorCode());
    }
}
