package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Blog;
import keapoint.onlog.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndCategoryOwner(String name, Blog blog);
    Optional<Category> findByIdAndCategoryOwner(long id, Blog categoryOwner);

    int countByCategoryOwner(Blog blog);
}