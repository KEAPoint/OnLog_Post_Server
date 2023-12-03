package keapoint.onlog.post.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokensDto {
    @Schema(name = "grantType", example = "Bearer", requiredProperties = "true", description = "토큰 타입")
    private String grantType;
    @Schema(name = "accessToken", example = "tokenString", requiredProperties = "true", description = "서비스 엑세스 토큰 스트링")
    private String accessToken;
    @Schema(name = "refreshToken", example = "tokenString", requiredProperties = "true", description = "서비스 리프레쉬 토큰 스트링")
    private String refreshToken;
}
