package com.pedrozc90.tenants.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import com.pedrozc90.core.audit.listeners.AuditListener;
import com.pedrozc90.users.models.User;
import com.querydsl.core.annotations.Config;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners({ AuditListener.class })
@Table(name = "tenants", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NamedNativeQueries({
    @NamedNativeQuery(name = "get_tenant", query = "SELECT get_tenant()"),
    @NamedNativeQuery(name = "set_tenant", query = "SELECT set_tenant(:tenant_id)"),
    @NamedNativeQuery(name = "reset_tenant", query = "CALL reset_tenant()")
})
@Config(entityAccessors = true, listAccessors = true, mapAccessors = true)
public class Tenant implements Serializable, Auditable {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tenants_id_seq")
    private Long id;

    @Embedded
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    public static Tenant merge(final Tenant t, final TenantData data) {
        t.setName(data.getName());
        t.setAudit(data.getAudit());
        return t;
    }

}
