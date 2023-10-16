package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.UserCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommentLikeRepository extends JpaRepository<UserCommentLike, Long> {
}
