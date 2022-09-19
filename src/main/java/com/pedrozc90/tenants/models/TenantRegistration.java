package com.pedrozc90.tenants.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Size(min = 1, max = 255)
    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

}
