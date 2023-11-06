package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 내가 팔로우 하고 있는 블로그 정보 조회
     *
     * @param me 내 블로그
     * @return 내가 팔로우 하고 있는 블로그 정보 조회
     */
    Optional<List<Follow>> findByMe(Blog me);

    Optional<Follow> findByMeAndTarget(Blog me, Blog target);

    /**
     * 특정 블로그가 구독하고 있는 다른 블로그의 수 구하기
     *
     * @param blog 구독자 수를 확인하려는 블로그
     * @return 해당 블로그가 구독하고 있는 블로그의 수
     */
    int countByMeAndFollowingIsTrue(Blog blog);
}
