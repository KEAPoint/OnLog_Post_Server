package keapoint.onlog.post.specification;

import keapoint.onlog.post.entity.Post;
import org.springframework.data.jpa.domain.Specification;

/**
 * 게시글에 대한 동적 쿼리 조건을 생성하는 클래스입니다.
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
}
