package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByMeAndTarget(Blog me, Blog target);

    /**
     * 특정 블로그가 구독하고 있는 다른 블로그의 수 구하기
     *
     * @param blog 구독자 수를 확인하려는 블로그
     * @return 해당 블로그가 구독하고 있는 블로그의 수
     */
    int countByMeAndFollowingIsTrue(Blog blog);
}
