package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.tenants.models.Tenant;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class UserRegistration {

    @NotNull
    @NotBlank
    @Email
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotBlank
    @JsonProperty("username")
    private String username;

    @NotNull
    @NotBlank
    @JsonProperty("password")
    private String password;

    @NotNull
    @NotBlank
    @JsonProperty("passwordConfirm")
    private String passwordConfirm;

    @JsonProperty("tenant")
    private Tenant tenant;

    public UserRegistration(final String email, final String username, final String password, final String passwordConfirm) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public static User transform(final UserRegistration data) {
        final User user = new User();
        user.setEmail(data.getEmail());
        user.setUsername(data.getUsername());
        user.setPassword(DigestUtils.md5Hex(data.getPassword()));
        user.setPasswordConfirm(data.getPasswordConfirm());
        user.setTenant(data.getTenant());
        return user;
    }

}
