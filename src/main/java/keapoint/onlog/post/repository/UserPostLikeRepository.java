package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Post;
import keapoint.onlog.post.entity.UserPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPostLikeRepository extends JpaRepository<UserPostLike, Long> {
    Optional<UserPostLike> findByBlogAndPost(Blog blog, Post post);

    void deleteByPost(Post post);
}

