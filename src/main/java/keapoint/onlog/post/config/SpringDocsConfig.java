package keapoint.onlog.post.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Swagger(Spring Docs) 설정
 */
@OpenAPIDefinition(
        info = @Info(title = "Onlog API 명세서",
                description = "Onlog API 명세서",
                version = "1.0.0")
)
@RequiredArgsConstructor
@Configuration
public class SpringDocsConfig {

    public OpenApiCustomizer securityBudiler() {
        return openApi -> openApi.addSecurityItem(new SecurityRequirement().addList("Access Token"))
                .getComponents()
                .addSecuritySchemes("Access Token", new SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION)
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .bearerFormat("JWT")
                        .scheme("bearer"));
    }

    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Onlog 서비스 API")
                .pathsToMatch(paths)
                .addOpenApiCustomizer(securityBudiler())
                .build();
    }
}
