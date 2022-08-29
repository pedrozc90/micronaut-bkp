package com.pedrozc90.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class ErrorMessage {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("path")
    private String path;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("message")
    private String message;

}
