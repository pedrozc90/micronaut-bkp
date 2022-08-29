package com.pedrozc90.core.tests;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

@Client("/")
public interface ApplicationClient {

    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    BearerAccessRefreshToken login(@Body final UsernamePasswordCredentials credentials);

    @Get("/secured")
    @Consumes(MediaType.TEXT_PLAIN)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    String secured(@Header(HttpHeaders.AUTHORIZATION) final String authorization);

}
