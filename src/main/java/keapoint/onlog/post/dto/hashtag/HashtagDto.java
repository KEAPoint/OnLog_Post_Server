package keapoint.onlog.post.dto.hashtag;

import keapoint.onlog.post.entity.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashtagDto {
    private Long id;
    private String name;

    public HashtagDto(Hashtag hashtag) {
        this.id = hashtag.getId();
        this.name = hashtag.getName();
    }
}
