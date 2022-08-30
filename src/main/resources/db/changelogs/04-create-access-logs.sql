-- liquibase formatted sql

-- changeset pedrozc90:1 context:dev,prd

CREATE SEQUENCE IF NOT EXISTS public.access_logs_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.access_logs (
    id          bigint DEFAULT nextval('access_logs_id_seq'),

    -- audit
    inserted_at timestamp without time zone NOT NULL DEFAULT current_timestamp,
    updated_at  timestamp without time zone NOT NULL DEFAULT current_timestamp,
    version     integer NOT NULL DEFAULT 1,

    user_agent  varchar(255),
    address     varchar(255),
    action      varchar(32) NOT NULL DEFAULT 'LOGIN',
    token       varchar(255),

    username    varchar(32),
    user_id     bigint,

    CONSTRAINT access_logs_pkey PRIMARY KEY (id)
    -- CONSTRAINT access_logs_user_fkey FOREIGN KEY (user_id) REFERENCES public.users (id)
);

-- rollback DROP TABLE IF EXISTS access_logs;
-- rollback DROP SEQUENCE IF EXISTS access_logs_id_seq;
