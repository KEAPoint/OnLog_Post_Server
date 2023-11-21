package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByName(String name);
}
