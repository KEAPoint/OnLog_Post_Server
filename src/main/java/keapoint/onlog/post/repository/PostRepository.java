package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Category;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    /**
     * 최신 게시글을 페이지네이션하여 가져옴
     *
     * @param status   게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByStatusAndIsPublic(Boolean status, Boolean isPublic, Pageable pageable);

    /**
     * 최신 게시글을 주제에 따라 페이지네이션하여 가져옴
     *
     * @param status   게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param topic    게시글 주제
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByStatusAndIsPublicAndTopic(Boolean status, Boolean isPublic, Topic topic, Pageable pageable);

    /**
     * 최신 게시글을 해시태그에 따라 페이지네이션하여 가져옴
     *
     * @param status   게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param hashtag  해시태그 이름
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    @Query("SELECT p FROM Post p JOIN p.hashtagList h WHERE p.status = :status AND p.isPublic = :isPublic AND h.name = :hashtagName")
    Page<Post> findByStatusAndIsPublicAndHashtag(Boolean status, Boolean isPublic, String hashtag, Pageable pageable);

    /**
     * 최신 게시글을 카테고리에 따라 페이지네이션하여 가져옴
     *
     * @param status   게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param category 게시글 카테고리
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByStatusAndIsPublicAndCategory(Boolean status, Boolean isPublic, Category category, Pageable pageable);

    /**
     * 나의 최근 비공개 게시글을 페이지네이션하여 가져옴
     *
     * @param writer   내 블로그
     * @param status   게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param pageable 페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByWriterAndStatusAndIsPublic(Blog writer, boolean status, boolean isPublic, Pageable pageable);
}
