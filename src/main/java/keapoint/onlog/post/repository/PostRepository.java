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
     * 주어진 상태와 공개 여부에 따른 게시글을 페이지네이션하여 가져옴
     * @param status 게시글의 유효성 여부
     * @param isPublic 게시글의 공개 여부
     * @param pageable 페이지네이션 정보를 담은 객체
     * @return 주어진 상태와 공개 여부에 따른 게시글의 페이지네이션 결과
     */
    Page<Post> findByStatusAndIsPublic(Boolean status, Boolean isPublic, Pageable pageable);
}
