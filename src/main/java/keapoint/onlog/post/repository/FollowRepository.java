package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByMeAndTarget(Blog me, Blog target);
}
