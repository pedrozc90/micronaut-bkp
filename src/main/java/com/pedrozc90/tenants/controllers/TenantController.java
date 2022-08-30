package com.pedrozc90.tenants.controllers;

import com.pedrozc90.core.models.ResultContent;
import com.pedrozc90.tenants.models.Tenant;
import com.pedrozc90.tenants.models.TenantData;
import com.pedrozc90.tenants.models.TenantRegistration;
import com.pedrozc90.tenants.repo.TenantRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.PersistenceException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Slf4j
@Secured(SecurityRule.IS_AUTHENTICATED)
@ExecuteOn(TaskExecutors.IO)
@Controller("/tenants")
public class TenantController {

    private final TenantRepository tenantRepository;

    public TenantController(final TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Get("/")
    public List<Tenant> fetch() {
        // final Long tenantId = AuthContext.getTenantId();
        tenantRepository.setAllTenantsSession();
        return tenantRepository.fetch();
    }

    @Post("/")
    public HttpResponse<Tenant> save(@Valid @Body final TenantRegistration data) {
        try {
            final Tenant t = TenantRegistration.transform(data);
            final Tenant tenant = tenantRepository.save(t);
            return HttpResponse
                .created(tenant)
                .headers((headers) -> headers.location(location(tenant.getId())));
        } catch (PersistenceException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.badRequest();
        }
    }

    @Put("/")
    public HttpResponse<Tenant> update(@NotNull @Valid @Body final TenantData data) {
        final Long id = data.getId();
        final Tenant tmp = tenantRepository.findByIdOrThrowException(id);
        final Tenant tenant = tenantRepository.update(tmp, data);
        return HttpResponse
            .ok(tenant)
            .headers((headers) -> headers.location(location(id)));
    }

    @Get("/{id}")
    public Tenant get(@NotNull @PathVariable final Long id) {
        return tenantRepository.findByIdOrThrowException(id);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@NotNull final Long id) {
        try {
            final Tenant tenant = tenantRepository.findByIdOrThrowException(id);
            tenantRepository.delete(tenant);
            final ResultContent<?> rs = ResultContent.of().message("Tenant (id: %s) successfully deleted.", tenant.getId());
            return HttpResponse.ok(rs);
        } catch (PersistenceException e) {
            return HttpResponse.badRequest();
        }
    }

    private URI location(final Long id) {
        return URI.create("/tenants/" + id);
    }

}
