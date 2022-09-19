package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
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

    @Positive
    @Schema(name = "id")
    @JsonProperty("id")
    private Long id;

    @Embedded
    @Schema(name = "audit")
    @JsonProperty("audit")
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Email
    @Size(min = 1, max = 255)
    @Schema(name = "email")
    @JsonProperty("email")
    private String email;

    @ToString.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(name = "profile")
    @JsonProperty("profile")
    private Profile profile = Profile.NORMAL;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @Schema(name = "username")
    @JsonProperty("username")
    private String username;

    @ToString.Include
    @NotNull
    @Schema(name = "active")
    @JsonProperty("active")
    private boolean active = true;

}
