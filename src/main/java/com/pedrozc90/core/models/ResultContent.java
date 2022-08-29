package com.pedrozc90.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResultContent<T> implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("message")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("data")
    private T data;

    public ResultContent() {
    }

    public ResultContent(T data) {
        this.data = data;
    }

    public static <T> ResultContent<T> of() {
        return new ResultContent<T>();
    }

    public static <T> ResultContent<T> of(final T data) {
        return new ResultContent<T>(data);
    }

    public ResultContent<T> message(final String message) {
        this.message = message;
        return this;
    }

    public ResultContent<T> message(final String fmt, final Object... args) {
        return message(String.format(fmt, args));
    }

}
