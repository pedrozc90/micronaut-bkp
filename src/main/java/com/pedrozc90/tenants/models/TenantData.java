package com.pedrozc90.tenants.models;

import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import io.micronaut.core.annotation.Introspected;
import lombok.*;

import javax.persistence.Embedded;
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
public class TenantData implements Serializable, Auditable {

    @NotNull
    private Long id;

    @Embedded
    private Audit audit = new Audit();

    @ToString.Include
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String name;

}
