package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * @param status    게시글의 유효성 여부
     * @param isPublic  게시글의 공개 여부
     * @param topicName 주제명
     * @param pageable  페이지 요청 정보 (페이지 번호, 페이지 크기 등)
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByStatusAndIsPublicAndTopicName(Boolean status, Boolean isPublic, String topicName, Pageable pageable);
}
