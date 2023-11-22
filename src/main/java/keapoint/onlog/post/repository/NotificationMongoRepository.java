// NotificationMongoRepository.java
package keapoint.onlog.post.repository;

import keapoint.onlog.post.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMongoRepository extends MongoRepository<Notification, String> {
    List<Notification> findByOp(String op);
}
