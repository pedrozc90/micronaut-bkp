package com.pedrozc90.core.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
public class Audit implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private ZonedDateTime insertedAt = ZonedDateTime.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    @Column(name = "updated_at", nullable = false, updatable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    public Integer getVersion() {
        if (version == null) return 1;
        return version;
    }

}
