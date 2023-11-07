package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("select max(c.ref) from Comment c where c.post =?1")
    Optional<Long> findMaxRefByPost(Post post);

    @Query("select sum(answerNum) from Comment where ref = ?1")
    Long getSumOfAnswerNum(Long ref);

    @Query("select max(step) from Comment where ref = ?1")
    Long getMaxStep(Long ref);

    @Modifying
    @Query("update Comment set refOrder = refOrder + 1 where ref = ?1 and refOrder > ?2")
    void updateRefOrder(Long ref, Long refOrder);
}