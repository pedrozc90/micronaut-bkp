package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Introspected
public class UserData implements Serializable, Auditable {

    @JsonProperty("id")
    private Long id;

    @Embedded
    @JsonProperty("audit")
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Email
    @Size(max = 255)
    @JsonProperty("email")
    private String email;

    @ToString.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonProperty("profile")
    private Profile profile = Profile.NORMAL;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 32)
    @JsonProperty("username")
    private String username;

    @ToString.Include
    @NotNull
    @JsonProperty("active")
    private boolean active = true;

}
