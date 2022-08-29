-- liquibase formatted sql

-- changeset pedrozc90:1 stripComments:true splitStatements:false context:dev,prd

CREATE OR REPLACE PROCEDURE reset_table_sequence(table_name text) AS $$
BEGIN
    EXECUTE format('SELECT setval(''%s_id_seq'', coalesce((SELECT max(t.id) + 1 FROM public.%s t), 1), false)', table_name, table_name);
END;
$$ LANGUAGE plpgsql;

-- rollback DROP FUNCTION IF EXISTS reset_table_sequence(table_name text);
