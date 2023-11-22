// Notification.java
package keapoint.onlog.post.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification")
public class Notification {
    @Id
    private String _id;
    private String op;
    private String type;
    private Object after;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowDetail {
        private String blog_id;
        private Long id;
        private boolean is_following;
        private String follow_id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetail {
        private String post_title;
        private Long post_hits;
        private String blog_id;
        private String post_thumbnail_link;
        private Long created_at;
        private Long post_likes_count;
        private boolean post_public;
        private String post_content;
        private Long category_id;
        private Long updated_at;
        private String post_id;
        private boolean post_modified;
        private Long topic_id;
        private boolean status;
        private String post_summary;
    }
}
