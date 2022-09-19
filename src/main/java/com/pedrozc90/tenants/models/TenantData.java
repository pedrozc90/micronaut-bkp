package com.pedrozc90.tenants.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrozc90.core.audit.Audit;
import com.pedrozc90.core.audit.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Size(min = 1, max = 255)
    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

}
