package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import com.pedrozc90.tenants.models.Tenant;
import com.querydsl.core.annotations.Config;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Entity
@EntityListeners({ AuditListener.class })
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
// @NamedNativeQueries({
//     @NamedNativeQuery(name = "reset_sequence", query = "CALL reset_table_sequence('users');")
// })
@Config(entityAccessors = true, listAccessors = true, mapAccessors = true)
public class User implements Serializable, Auditable {

    @ToString.Include
    @Id
    // @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", schema = "public", initialValue = 1)
    @Schema(name = "id")
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "users_id_seq")
    private Long id;

    @Embedded
    @Schema(name = "audit")
    @JsonProperty("audit")
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(name = "email")
    @JsonProperty("email")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ToString.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(name = "profile")
    @JsonProperty("profile")
    @Column(name = "profile", length = 16, nullable = false)
    private Profile profile = Profile.NORMAL;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 32)
    @Schema(name = "username")
    @JsonProperty("username")
    @Column(name = "username", length = 32, nullable = false, unique = true)
    private String username;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 32)
    @Schema(name = "password")
    @Column(name = "password", length = 32, nullable = false)
    private String password;

    @Transient
    private String passwordConfirm;

    @ToString.Include
    @NotNull
    @Schema(name = "active")
    @JsonProperty("active")
    @Column(name = "active", columnDefinition = "boolean", nullable = false)
    private boolean active = true;

    @JsonInclude(ALWAYS)
    @Schema(name = "tenant")
    @JsonProperty("tenant")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", foreignKey = @ForeignKey(name = "users_tenant_fkey"))
    private Tenant tenant;

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(final String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    @JsonProperty("password_confirm")
    public void setPasswordConfirm(final String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @JsonIgnore
    public boolean isNotActive() {
        return !isActive();
    }

}
