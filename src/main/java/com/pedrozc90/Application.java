package com.pedrozc90;

import io.micronaut.openapi.annotation.OpenAPIInclude;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "jwt",
    paramName = "token"
)
@OpenAPIDefinition(
    info = @Info(
        title = "blank",
        version = "1.0"
    )
)
@OpenAPIInclude(
    classes = {
        io.micronaut.security.endpoints.LoginController.class,
        io.micronaut.security.endpoints.LogoutController.class,
        io.micronaut.security.token.jwt.endpoints.OauthController.class
    },
    tags = @Tag(name = "Security")
)
@SecurityRequirement(name = "BearerAuth")
public class Application {

    public static void main(final String[] args) {
        Micronaut.run(Application.class, args);
    }

}
