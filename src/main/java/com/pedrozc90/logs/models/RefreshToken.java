package com.pedrozc90.logs.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners({ AuditListener.class })
@Table(name = "refresh_token", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RefreshToken implements Serializable, Auditable {

    @ToString.Include
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "refresh_token_id_seq")
    private Long id;

    @Embedded
    @Column(name = "audit")
    private Audit audit = new Audit();

    @NotNull
    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @ToString.Include
    @NotNull
    @NotBlank
    @Column(name = "refresh_token", columnDefinition = "text")
    private String refreshToken;

    @ToString.Include
    @NotNull
    @Column(name = "revoked", columnDefinition = "boolean", nullable = false)
    private boolean revoked = false;

}
