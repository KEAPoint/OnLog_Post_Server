package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPostLikeRepository extends JpaRepository<UserPostLike, Long> {
}

