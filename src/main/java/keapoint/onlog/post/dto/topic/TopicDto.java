package keapoint.onlog.post.dto.topic;

import keapoint.onlog.post.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private Long id;
    private String name;

    public TopicDto(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
    }
}
