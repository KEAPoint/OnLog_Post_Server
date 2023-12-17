package keapoint.onlog.post.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class SocialAccountUserInfo {
    private final String userName;
    private final String userEmail;
    private final String profileImgUrl;
}