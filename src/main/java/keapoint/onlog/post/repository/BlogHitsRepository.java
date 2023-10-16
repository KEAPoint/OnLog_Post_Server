package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.BlogHits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlogHitsRepository extends JpaRepository<BlogHits, UUID> {
}
