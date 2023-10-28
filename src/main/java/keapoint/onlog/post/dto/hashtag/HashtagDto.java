package keapoint.onlog.post.dto.hashtag;

import keapoint.onlog.post.entity.Hashtag;
import lombok.Data;

@Data
public class HashtagDto {
    private Long id;
    private String name;

    public HashtagDto(Hashtag hashtag) {
        this.id = hashtag.getId();
        this.name = hashtag.getName();
    }
}
