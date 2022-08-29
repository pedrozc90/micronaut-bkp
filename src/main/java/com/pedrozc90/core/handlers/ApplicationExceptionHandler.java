package com.pedrozc90.core.handlers;

import com.pedrozc90.core.exceptions.ApplicationException;
import com.pedrozc90.core.models.ErrorMessage;
import com.pedrozc90.core.models.ResultContent;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Produces
@Singleton
@Requires(classes = { ApplicationException.class, ExceptionHandler.class })
public class ApplicationExceptionHandler implements ExceptionHandler<ApplicationException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(final HttpRequest request, @NotNull final ApplicationException exception) {
        final HttpStatus status = (exception.getStatus() != null) ? exception.getStatus() : HttpStatus.NOT_FOUND;
        // final List<ErrorMessage> list = getErrorList(exception);
        final ResultContent<?> rs = ResultContent.of().message(exception.getMessage());
        return HttpResponse.status(status).body(rs);
    }

    private List<ErrorMessage> getErrorList(ApplicationException e) {
        final List<ErrorMessage> list = new ArrayList<>();
        Throwable t = e;
        do {
            list.add(ErrorMessage.builder()
                .message(e.getMessage())
                .path(e.getField())
                .build());
            t = t.getCause();
        } while (nonNull(t) && t != t.getCause());
        return list;
    }

}
