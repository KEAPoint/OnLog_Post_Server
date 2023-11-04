package keapoint.onlog.post.specification;

import keapoint.onlog.post.entity.Post;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * 게시글에 대한 동적 쿼리 조건을 생성하는 클래스
 */
public class PostSpecification {

    /**
     * 주제 이름에 대한 게시글을 검색하는 Specification
     *
     * @param topicName 주제 이름
     * @return 주제 이름에 대한 Specification
     */
    public static Specification<Post> withTopicName(String topicName) {
        return (root, query, criteriaBuilder) -> {
            if (topicName != null && !topicName.isEmpty()) {
                return criteriaBuilder.equal(root.get("topic").get("name"), topicName);
            }
            return null;
        };
    }

    /**
     * 해시태그에 대한 게시글을 검색하는 Specification
     *
     * @param hashtag 해시태그
     * @return 해시태그에 따른 게시글 검색 조건
     */
    public static Specification<Post> withHashtag(String hashtag) {
        return (root, query, criteriaBuilder) -> {
            if (hashtag != null && !hashtag.isEmpty()) {
                return criteriaBuilder.isMember(hashtag, root.get("hashtagList"));
            }
            return null;
        };
    }

    /**
     * 카테고리 ID에 대한 Specification
     *
     * @param categoryId 카테고리 ID
     * @return 카테고리 ID에 대한 Specification
     */
    public static Specification<Post> withCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId != null) {
                return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
            }
            return null;
        };
    }

    /**
     * 블로그 식별자에 대한 Specification
     *
     * @param blogId 블로그 식별자
     * @return 블로그 식별자에 대한 Specification
     */
    public static Specification<Post> withBlogId(UUID blogId) {
        return (root, query, criteriaBuilder) -> {
            if (blogId != null) {
                return criteriaBuilder.equal(root.get("writer").get("blogId"), blogId);
            }
            return null;
        };
    }

    /**
     * 게시글 유효성에 대한 Specification
     *
     * @return 게시글 유효성에 대한 Specification
     */
    public static Specification<Post> withStatusTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("status"));
    }

    /**
     * 게시글 공개 여부에 대한 Specification
     *
     * @param isPublic 게시글 공개 여부
     * @return 게시글 공개 여부에 대한 Specification
     */
    public static Specification<Post> withIsPublic(Boolean isPublic) {
        return (root, query, criteriaBuilder) -> {
            if (isPublic != null) {
                return criteriaBuilder.equal(root.get("isPublic"), isPublic);
            }
            return null;
        };
    }
}
