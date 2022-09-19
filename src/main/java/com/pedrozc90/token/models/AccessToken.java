package com.pedrozc90.token.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners({ AuditListener.class })
@Table(name = "access_token", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AccessToken implements Serializable, Auditable {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "access_token_id_seq")
    private Long id;

    @Embedded
    private Audit audit = new Audit();

    @ToString.Include
    @Column(name = "user_agent")
    private String userAgent;

    @ToString.Include
    @Column(name = "address")
    private String address;

    @ToString.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 32, nullable = false)
    private AccessAction action = AccessAction.LOGIN;

    @ToString.Include
    @Column(name = "username", length = 32, nullable = false)
    private String username;

    @ToString.Include
    @Column(name = "access_token", columnDefinition = "text")
    private String accessToken;

    @ToString.Include
    @Column(name = "refresh_token", columnDefinition = "text")
    private String refreshToken;

}
