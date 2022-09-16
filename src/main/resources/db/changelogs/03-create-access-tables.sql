-- liquibase formatted sql

-- changeset pedrozc90:1 context:dev,prd

CREATE SEQUENCE IF NOT EXISTS public.access_token_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.access_token (
    id              bigint DEFAULT nextval('access_token_id_seq'),

    -- audit
    inserted_at     timestamp without time zone NOT NULL DEFAULT current_timestamp,
    updated_at      timestamp without time zone NOT NULL DEFAULT current_timestamp,
    version         integer NOT NULL DEFAULT 1,

    user_agent      varchar(255),
    address         varchar(255),
    action          varchar(32) NOT NULL DEFAULT 'LOGIN',
    username        varchar(32),
    access_token    text NOT NULL,
    refresh_token   text NOT NULL,

    CONSTRAINT access_token_pkey PRIMARY KEY (id)
);

-- rollback DROP TABLE IF EXISTS access_token;
-- rollback DROP SEQUENCE IF EXISTS access_token_id_seq;

-- changeset pedrozc90:2 context:dev,prd

CREATE SEQUENCE IF NOT EXISTS public.refresh_token_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.refresh_token (
    id              bigint DEFAULT nextval('refresh_token_id_seq'),

    -- audit
    inserted_at     timestamp without time zone NOT NULL DEFAULT current_timestamp,
    updated_at      timestamp without time zone NOT NULL DEFAULT current_timestamp,
    version         integer NOT NULL DEFAULT 1,

    username        varchar(32),
    refresh_token   text NOT NULL,
    revoked         boolean NOT NULL default false,

    CONSTRAINT refresh_token_pkey PRIMARY KEY (id)
);

-- rollback DROP TABLE IF EXISTS refresh_token;
-- rollback DROP SEQUENCE IF EXISTS refresh_token_id_seq;