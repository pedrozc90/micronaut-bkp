package com.pedrozc90.core.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    protected HttpStatus status = HttpStatus.BAD_REQUEST;

    protected String field;

    public ApplicationException() {
        super();
    }

    public ApplicationException(final String message) {
        super(message);
    }

    public ApplicationException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApplicationException(final String message, final String field) {
        super(message);
        this.field = field;
    }

    public ApplicationException(final String message, final HttpStatus status, final String field) {
        super(message);
        this.status = status;
        this.field = field;
    }

    public ApplicationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public ApplicationException(final String message, final HttpStatus status, final Throwable throwable) {
        super(message, throwable);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public String getField() {
        return field;
    }

    public static ApplicationException of(final String message) {
        return new ApplicationException(message);
    }

    public static ApplicationException of(final String fmt, final Object... args) {
        return new ApplicationException(String.format(fmt, args));
    }

    // public static ApplicationException of(final String message, final HttpStatus status) {
    //     return new ApplicationException(message, status);
    // }

    // public static ApplicationException of() {
    //     return new ApplicationException();
    // }

    @JsonIgnore
    public ApplicationException ok() {
        this.status = HttpStatus.OK;
        return this;
    }

    @JsonIgnore
    public ApplicationException badRequest() {
        this.status = HttpStatus.BAD_REQUEST;
        return this;
    }

    @JsonIgnore
    public ApplicationException notFound() {
        this.status = HttpStatus.NOT_FOUND;
        return this;
    }

}
