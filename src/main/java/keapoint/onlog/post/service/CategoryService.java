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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogRepository blogRepository;

    private final TopicRepository topicRepository;

    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(UUID blogId, PostCreateCategoryReqDto dto) throws BaseException {
        try {
            // 사용자 조회
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.BLOG_NOT_FOUND_EXCEPTION));

            // 사용자가 해당 이름으로 카테고리를 만든 적 있는지 조회
            if (categoryRepository.findByNameAndCategoryOwner(dto.getName(), blog).isPresent()) // 이미 사용자가 해당 이름으로 카테고리를 가지고 있는 경우
                throw new BaseException(BaseErrorCode.ALREADY_CATEGORY_EXISTS_EXCEPTION);

            // 토픽 조회
            Topic topic = topicRepository.findById(dto.getTopicId())
                    .orElseThrow(() -> new BaseException(BaseErrorCode.TOPIC_NOT_FOUND_EXCEPTION));

            // order 필드 세팅
            int order = categoryRepository.countByCategoryOwner(blog) + 1;

            Category newCategory = Category.builder()
                    .name(dto.getName())
                    .order(order)
                    .topic(topic)
                    .categoryOwner(blog)
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
    public CategoryDto updateCategory(Long categoryId, PostCreateCategoryReqDto dto) throws BaseException {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));

            // 카테고리 이름 변경 전 이미 같은 이름의 카테고리가 있는지 확인
            if (categoryRepository.findByNameAndCategoryOwner(dto.getName(), category.getCategoryOwner()).isPresent())
                throw new BaseException(BaseErrorCode.ALREADY_CATEGORY_EXISTS_EXCEPTION);

            category.setName(dto.getName());
            categoryRepository.save(category);
            log.info("수정된 카테고리: " + category);


            return new CategoryDto(category);
        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteCategory(Long categoryId) throws BaseException {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BaseException(BaseErrorCode.CATEGORY_NOT_FOUND_EXCEPTION));
            categoryRepository.delete(category);
            log.info("삭제된 카테고리: " + category);
        } catch (BaseException e) {
            log.error(e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}