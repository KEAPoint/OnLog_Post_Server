package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Category;
import keapoint.onlog.post.entity.Topic;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CategoryRepository;
import keapoint.onlog.post.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import keapoint.onlog.post.dto.category.PutCategoryUpdateReqDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogRepository blogRepository;

    private final TopicRepository topicRepository;

    private final CategoryRepository categoryRepository;

    /**
     * 새 카테고리를 생성하는 메소드
     *
     * @param blogId 카테고리를 생성할 블로그의 ID
     * @param dto    카테고리 생성 요청 데이터
     * @return 생성된 카테고리 정보
     */
    public CategoryDto createCategory(UUID blogId, PostCreateCategoryReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 사용자가 해당 이름으로 카테고리를 만든 적 있는지 조회
            if (blog.getCategories().stream().anyMatch(category -> category.getName().equals(dto.getName()))) // 이미 사용자가 해당 이름으로 카테고리를 가지고 있는 경우
                throw new BaseException(BaseErrorCode.ALREADY_CATEGORY_EXISTS_EXCEPTION);

            Category newCategory = Category.builder()
                    .name(dto.getName())
                    .order(blog.getCategories().size() + 1) // 생성된 카테고리 순서는 가장 마지막
                    .build();

            Category category = categoryRepository.save(newCategory);
            log.info("생성된 카테고리: " + category);

            return new CategoryDto(category);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 기존 카테고리를 수정하는 메소드
     *
     * @param blogId 사용자의 블로그 식별자
     * @param dto    카테고리 수정 요청 데이터
     * @return 수정된 카테고리 정보
     */
    // 카테고리 수정 API
    public CategoryDto updateCategory(UUID blogId, PutCategoryUpdateReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 카테고리 조회
            Category category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 사용자가 해당 카테고리를 가지고 있는지 조회
            // 사용자가 만든 카테고리가 아니라면, UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION
            if (blog.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 카테고리 이름 수정
            category.updateCategory(dto.getName());
            log.info("수정된 카테고리: " + category);

            // 수정된 카테고리 반환
            return new CategoryDto(category);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 기존 카테고리를 삭제하는 메소드
     *
     * @param blogId 사용자의 블로그 식별자
     * @param dto    카테고리 삭제 요청 데이터
     * @return 삭제된 카테고리 정보
     */
    public CategoryDto deleteCategory(UUID blogId, DeleteCategoryReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 카테고리 조회
            Category category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 사용자가 해당 카테고리를 가지고 있는지 조회
            // 사용자가 만든 카테고리가 아니라면, UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION
            if (blog.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 카테고리 삭제
            categoryRepository.delete(category);
            log.info("삭제된 카테고리: " + category);

            // 삭제된 카테고리 반환
            return new CategoryDto(category);

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}