package com.pedrozc90.tenants.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class TenantRegistration {

    @NotNull
    @NotBlank
    @Size(max = 255)
    @JsonProperty("name")
    private String name;

    public static Tenant transform(final TenantRegistration data) {
        final Tenant t = new Tenant();
        t.setName(data.getName());
        return t;
    }

}
