package com.pedrozc90.logs.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "access_token_id_seq")
    private Long id;

    @Embedded
    @Column(name = "audit")
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

    @NotNull
    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @ToString.Include
    @NotNull
    @Column(name = "access_token", columnDefinition = "text")
    private String accessToken;

    @ToString.Include
    @NotNull
    @Column(name = "refresh_token", columnDefinition = "text")
    private String refreshToken;

    public void setUserAgent(final String userAgent) {
        this.userAgent = StringUtils.substring(userAgent, 0, 255);
    }

    public void setAddress(final String address) {
        this.address = StringUtils.substring(address, 0, 255);
    }

}
