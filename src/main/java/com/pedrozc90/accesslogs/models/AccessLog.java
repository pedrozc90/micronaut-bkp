package com.pedrozc90.accesslogs.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@EntityListeners({ AuditListener.class })
@Table(name = "access_logs", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AccessLog implements Serializable, Auditable {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "access_logs_id_seq")
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
    @Column(name = "token")
    private String token;

    @Column(name = "username")
    private String username;

    @Column(name = "user_id")
    private Long userId;

    public void setUserAgent(final String userAgent) {
        this.userAgent = StringUtils.substring(userAgent, 0, 255);
    }

    public void setAddress(final String address) {
        this.address = StringUtils.substring(address, 0, 255);
    }

    public void setToken(final String token) {
        this.token = StringUtils.substring(token, 0, 255);
    }

}
