package com.pedrozc90.core.handlers;

import com.pedrozc90.core.exceptions.TokenNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;

@Produces
@Singleton
@Requires(classes = { TokenNotFoundException.class, ExceptionHandler.class })
public class TokenNotFoundExceptionHandler implements ExceptionHandler<TokenNotFoundException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(final HttpRequest request, @NotNull final TokenNotFoundException exception) {
        return HttpResponse.status(HttpStatus.FORBIDDEN);
    }

}
