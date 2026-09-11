package br.com.useautentica.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados exibidos no Swagger UI (/swagger-ui.html) e esquema de
 * autenticação Bearer, que habilita o botão "Authorize" pra testar
 * endpoints de /api/admin/** direto pela UI, colando o token JWT retornado
 * por POST /api/auth/login.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Use Autêntica API",
                version = "v1",
                description = "API da vitrine digital e área administrativa da loja Use Autêntica."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
