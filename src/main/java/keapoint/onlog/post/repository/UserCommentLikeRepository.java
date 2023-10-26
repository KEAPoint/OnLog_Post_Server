package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.UserCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCommentLikeRepository extends JpaRepository<UserCommentLike, Long> {
    Optional<UserCommentLike> findByBlogAndComment(Blog blog, Comment comment);
}
