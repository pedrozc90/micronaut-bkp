package com.pedrozc90.core.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
public class Audit implements Serializable {

    @Schema(name = "inserted_at")
    @JsonProperty("inserted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private ZonedDateTime insertedAt = ZonedDateTime.now();

    @Schema(name = "updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    @Column(name = "updated_at", nullable = false, updatable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @NotNull
    @Positive
    @Min(value = 1)
    @Schema(name = "version")
    @JsonProperty("version")
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    public Integer getVersion() {
        if (version == null) return 1;
        return version;
    }

}
