-- changeset gravita:1772352082091-1
ALTER TABLE public.profiles
    ADD deleted bool DEFAULT false NOT NULL;
-- changeset gravita:1772352082091-2
ALTER TABLE public.update_directories
    ADD update_at timestamp NULL;
-- changeset gravita:1772352082091-3
ALTER TABLE public.update_directories
    ADD unconnected_name VARCHAR(255) NULL;