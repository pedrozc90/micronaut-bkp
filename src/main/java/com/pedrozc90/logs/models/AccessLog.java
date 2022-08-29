package com.pedrozc90.logs.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import com.pedrozc90.users.models.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "access_logs_user_fkey"))
    @JoinColumn(name = "user_id")
    private User user;

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
