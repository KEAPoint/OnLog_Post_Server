package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
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
import keapoint.onlog.post.dto.category.PutCategoryUpdateReqDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import java.util.Optional;

@Rollback
@SpringBootTest
class CategoryServiceUnitTest {

    // Mock 객체로 선언하여 실제 객체 대신 가짜 객체를 사용합니다.
    @Mock
    private CategoryRepository categoryRepository;

    // Spy와 InjectMocks 어노테이션을 사용하여 CategoryService에 가짜 객체를 주입합니다.
    @Spy
    @InjectMocks
    private CategoryService categoryService;

    // Mock 객체로 선언하여 실제 객체 대신 가짜 객체를 사용합니다.
    @Mock
    private BlogRepository blogRepository;

    @BeforeEach
    void 데이터_삭제() {
        // Mockito 어노테이션을 초기화합니다.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 카테고리_생성() throws BaseException {

        // 블로그 ID를 랜덤으로 생성하고, 카테고리 이름을 설정합니다.
        UUID blogId = UUID.randomUUID();
        PostCreateCategoryReqDto reqDto = new PostCreateCategoryReqDto();
        reqDto.setName("Tech");

        // Blog와 Category 객체를 생성합니다.
        Blog blog = new Blog();
        Category category = new Category();

        // blogRepository의 findById 메서드가 호출되면 생성해둔 Blog 객체를 반환하도록 설정합니다.
        when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        // categoryRepository의 save 메서드가 호출되면 생성해둔 Category 객체를 반환하도록 설정합니다.
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        try {
            // 카테고리 생성 서비스를 호출합니다.
            CategoryDto createdCategory = categoryService.createCategory(blogId, reqDto);

            // 생성된 카테고리의 존재와 이름을 확인합니다.
            assertNotNull(createdCategory);
            assertEquals(createdCategory.getName(), reqDto.getName());
        } catch (BaseException e) {
            // 에러가 발생하면 테스트를 실패하도록 합니다.
            fail(e.getMessage());
        }
    }

    @Test
    void 카테고리_삭제() throws BaseException {

        // 블로그 ID를 랜덤으로 생성하고, 카테고리 이름을 설정합니다.
        UUID blogId = UUID.randomUUID();
        PostCreateCategoryReqDto createReqDto = new PostCreateCategoryReqDto();
        createReqDto.setName("Tech");

        // 삭제할 카테고리의 ID를 설정합니다.
        DeleteCategoryReqDto deleteReqDto = new DeleteCategoryReqDto();
        deleteReqDto.setId(1L);

        // Blog와 Category 객체를 생성합니다.
        Blog blog = new Blog();
        Category category = new Category();

        // blogRepository의 findById 메서드가 호출되면 생성해둔 Blog 객체를 반환하도록 설정합니다.
        when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));
        // categoryRepository의 findById 메서드가 호출되면 생성해둔 Category 객체를 반환하도록 설정합니다.
        when(categoryRepository.findById(deleteReqDto.getId())).thenReturn(Optional.of(category));

        try {
            // 카테고리를 생성하고 삭제합니다.
            CategoryDto createdCategory = categoryService.createCategory(blogId, createReqDto);
            categoryService.deleteCategory(blogId, deleteReqDto);

            // 카테고리가 실제로 삭제되었는지 확인합니다.
            verify(categoryRepository, times(1)).deleteById(deleteReqDto.getId());
        } catch (BaseException e) {
            // 에러가 발생하면 테스트를 실패하도록 합니다.
            fail(e.getMessage());
        }
    }
}
