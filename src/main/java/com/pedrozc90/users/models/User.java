package com.pedrozc90.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

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
@NamedNativeQueries({
    @NamedNativeQuery(name = "reset_sequence", query = "CALL reset_table_sequence('users');")
})
public class User implements Serializable, Auditable {

    @ToString.Include
    @Id
    // @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", schema = "public", initialValue = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "users_id_seq")
    private Long id;

    @Embedded
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ToString.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "profile", length = 16, nullable = false)
    private Profile profile = Profile.NORMAL;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 32)
    @Column(name = "username", length = 32, nullable = false, unique = true)
    private String username;

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 32)
    @Column(name = "password", length = 32, nullable = false)
    private String password;

    @Transient
    private String passwordConfirm;

    @ToString.Include
    @NotNull
    @Column(name = "active", columnDefinition = "boolean", nullable = false)
    private boolean active = true;

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

    @JsonProperty("passwordConfirm")
    public void setPasswordConfirm(final String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @JsonIgnore
    public boolean isNotActive() {
        return !isActive();
    }

    public static User merge(final User user, final UserData data) {
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setProfile(data.getProfile());
        user.setActive(data.isActive());
        user.setAudit(data.getAudit());
        return user;
    }

}
