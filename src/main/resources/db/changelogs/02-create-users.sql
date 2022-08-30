-- liquibase formatted sql

-- changeset pedrozc90:1 context:dev,prd

CREATE SEQUENCE IF NOT EXISTS public.users_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS public.users (
    id          bigint DEFAULT nextval('users_id_seq'),

    -- audit
    inserted_at timestamp without time zone NOT NULL DEFAULT current_timestamp,
    updated_at  timestamp without time zone NOT NULL DEFAULT current_timestamp,
    version     integer NOT NULL DEFAULT 1,

    email       varchar(255) NOT NULL,
    profile     varchar(16) NOT NULL DEFAULT 'NORMAL',
    username    varchar(32) NOT NULL,
    password    varchar(32) NOT NULL,
    active      boolean NOT NULL DEFAULT true,

    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_ukey UNIQUE (username),
    CONSTRAINT users_email_ukey UNIQUE (email)
);

INSERT INTO public.users (email, profile, username, password) VALUES
('admin@email.com', 'MASTER', 'master', md5('1'));

INSERT INTO public.users (email, profile, username, password, active) VALUES
('test@email.com', 'NORMAL', 'tester', md5('1'), false);

-- rollback DROP TABLE IF EXISTS users;
-- rollback DROP SEQUENCE IF EXISTS users_id_seq;
