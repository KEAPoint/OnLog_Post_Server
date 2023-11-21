package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Comment;
import keapoint.onlog.post.entity.UserCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCommentLikeRepository extends JpaRepository<UserCommentLike, Long> {
    Optional<UserCommentLike> findByBlogAndComment(Blog blog, Comment comment);

    /**
     * 블로그와 유효한(삭제되지 않은) 댓글 목록에 해당하는 좋아요 정보를 조회한다.
     *
     * @param blog     좋아요 정보를 조회할 블로그
     * @param comments 좋아요 정보를 조회할 댓글 목록
     * @return 블로그와 유효한 댓글 목록에 해당하는 좋아요 정보 리스트
     */
    @Query("SELECT u FROM UserCommentLike u WHERE u.blog = :blog AND u.comment IN :comments AND u.comment.status = true")
    List<UserCommentLike> findByBlogAndValidComments(@Param("blog") Blog blog, @Param("comments") List<Comment> comments);

    void deleteByComment(Comment comment);
}

