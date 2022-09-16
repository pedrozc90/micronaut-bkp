package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Introspected
public class UserRegistration {

    @NotNull
    @NotBlank
    @Email
    @Size(min = 1, max = 255)
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @JsonProperty("username")
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @JsonProperty("password")
    private String password;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @JsonProperty("password_confirm")
    private String passwordConfirm;

}
