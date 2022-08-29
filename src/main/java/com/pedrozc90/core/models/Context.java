package com.pedrozc90.core.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.users.models.User;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Introspected
public class Context {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("user")
    private User user;

}
