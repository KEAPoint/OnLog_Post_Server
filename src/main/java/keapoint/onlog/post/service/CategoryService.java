package keapoint.onlog.post.service;

import keapoint.onlog.post.base.BaseErrorCode;
import keapoint.onlog.post.base.BaseException;
import keapoint.onlog.post.dto.category.CategoryDto;
import keapoint.onlog.post.dto.category.PostCreateCategoryReqDto;
import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Category;
import keapoint.onlog.post.repository.BlogRepository;
import keapoint.onlog.post.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import keapoint.onlog.post.dto.category.PutCategoryUpdateReqDto;
import keapoint.onlog.post.dto.category.DeleteCategoryReqDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 조회
     *
     * @param blogId 조회하고자 하는 블로그 식별자
     * @return 특정 유저의 카테고리 정보
     */
    public List<CategoryDto> getCategories(UUID blogId) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("카테고리 조회할 블로그 정보: " + blog.toString());

            return blog.getCategories().stream()
                    .map(CategoryDto::new)
                    .toList();

        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 카테고리 생성
     *
     * @param blogId 카테고리 생성을 원하는 블로그 식별자
     * @param dto    생성하고자 하는 카테고리 정보
     * @return 생성된 카테고리 정보
     */
    public CategoryDto createCategory(UUID blogId, PostCreateCategoryReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("카테고리 생성할 블로그 정보: " + blog.toString());

            // 사용자가 해당 이름으로 카테고리를 만든 적 있는지 조회
            if (blog.getCategories().stream().anyMatch(category -> category.getName().equals(dto.getName()))) // 이미 사용자가 해당 이름으로 카테고리를 가지고 있는 경우
                throw new BaseException(BaseErrorCode.ALREADY_CATEGORY_EXISTS_EXCEPTION);

            Category newCategory = Category.builder()
                    .name(dto.getName())
                    .order(blog.getCategories().size() + 1) // 생성된 카테고리 순서는 가장 마지막
                    .build();

            // 카테고리 생성
            Category category = categoryRepository.save(newCategory);
            blog.getCategories().add(category);

            log.info("생성된 카테고리 정보: " + category);

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
     * 카테고리 수정
     *
     * @param blogId 카테고리 수정을 원하는 블로그 식별자
     * @param dto    수정하고자 하는 카테고리 정보
     * @return 수정된 카테고리 정보
     */
    public CategoryDto updateCategory(UUID blogId, PutCategoryUpdateReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("카테고리 수정할 블로그 정보: " + blog.toString());

            // 카테고리 조회
            Category category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
            log.info("수정할 카테고리 정보: " + category.toString());

            // 사용자가 해당 카테고리를 가지고 있는지 조회
            // 사용자가 만든 카테고리가 아니라면, UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION
            if (!blog.getCategories().contains(category))
                throw new BaseException(BaseErrorCode.UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION);

            // 카테고리 이름 수정
            category.updateCategory(dto.getName());
            log.info("수정된 카테고리 정보: " + category);

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
     * 카테고리 삭제
     *
     * @param blogId 카테고리 삭제를 원하는 블로그 식별자
     * @param dto    삭제하고자 하는 카테고리 정보
     * @return 삭제된 카테고리 정보
     */
    public CategoryDto deleteCategory(UUID blogId, DeleteCategoryReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));
            log.info("카테고리 삭제할 블로그 정보: " + blog.toString());

            // 카테고리 조회
            Category category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
            log.info("삭제할 카테고리 정보: " + category.toString());

            // 사용자가 해당 카테고리를 가지고 있는지 조회
            // 사용자가 만든 카테고리가 아니라면, UNAUTHORIZED_CATEGORY_ACCESS_EXCEPTION
            if (!blog.getCategories().contains(category))
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