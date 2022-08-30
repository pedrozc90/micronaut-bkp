-- liquibase formatted sql

-- changeset pedrozc90:1 context:dev,prd

CREATE SEQUENCE IF NOT EXISTS public.tenants_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.tenants (
    id          bigint DEFAULT nextval('tenants_id_seq'),

    -- audit
    inserted_at timestamp without time zone NOT NULL DEFAULT current_timestamp,
    updated_at  timestamp without time zone NOT NULL DEFAULT current_timestamp,
    version     integer NOT NULL DEFAULT 1,

    name        varchar(255) NOT NULL,
    CONSTRAINT tenants_pkey PRIMARY KEY (id),
    CONSTRAINT tenants_name_ukey UNIQUE (name)
);

INSERT INTO public.tenants (name) VALUES
('Debug'),
('Test');

-- rollback DROP TABLE IF EXISTS tenants;
-- rollback DROP SEQUENCE IF EXISTS tenants_id_seq;

-- changeset pedrozc90:2 splitStatements:false context:dev,prd

CREATE OR REPLACE FUNCTION get_tenant() RETURNS bigint AS $$
DECLARE
    tenant_id text;
BEGIN
    SELECT current_setting('db.tenant', true) INTO tenant_id;
    IF (tenant_id = '') THEN
        tenant_id := -1;
    END IF;
    RETURN tenant_id::bigint;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_all_tenants() RETURNS bigint AS $$
BEGIN
    RETURN -1::bigint;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_tenant(tenant_id bigint) RETURNS bigint
    AS 'SELECT set_config(''db.tenant'', tenant_id::text, false)::bigint'
    LANGUAGE SQL;

CREATE OR REPLACE PROCEDURE reset_tenant()
AS 'RESET db.tenant'
    LANGUAGE SQL;

-- rollback DROP FUNCTION IF EXISTS get_tenant();
-- rollback DROP FUNCTION IF EXISTS set_tenant(tenant_id bigint);
-- rollback DROP FUNCTION IF EXISTS get_all_tenants();

-- changeset pedrozc90:3 splitStatements:false context:dev,prd

CREATE OR REPLACE VIEW vw_tenants AS
SELECT * FROM tenants t
WHERE t.id = get_tenant()
   OR get_tenant() = get_all_tenants();
