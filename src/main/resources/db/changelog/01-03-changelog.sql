-- liquibase formatted sql

-- changeset gravita:1772352082091-1
ALTER TABLE public.profiles
    ADD deleted bool DEFAULT false NOT NULL;
-- changeset gravita:1772352082091-2
ALTER TABLE public.update_directories
    ADD update_at timestamp NULL;
-- changeset gravita:1772352082091-3
ALTER TABLE public.update_directories
    ADD unconnected_name VARCHAR(255) NULL;

-- changeset gravita:1772355934103-1
CREATE SEQUENCE IF NOT EXISTS launcher_artifacts_seq START WITH 1 INCREMENT BY 1;
-- changeset gravita:1772355934103-2
CREATE TABLE launcher_artifacts
(
    id            BIGINT NOT NULL,
    artifact_type VARCHAR(255),
    upload_at     TIMESTAMP WITHOUT TIME ZONE,
    public_key    BYTEA,
    CONSTRAINT pk_launcher_artifacts PRIMARY KEY (id)
);
-- changeset gravita:1772355934103-3
ALTER TABLE public.launcher_artifacts
    ADD deprecated bool DEFAULT false NOT NULL;
-- changeset gravita:1772355934103-4
ALTER TABLE public.launcher_artifacts
    ADD artifact_id VARCHAR(255) NULL;
-- changeset gravita:1772355934103-5
ALTER TABLE update_profiles
    DROP CONSTRAINT fk_update_profiles_on_profile;
ALTER TABLE public.update_profiles
    ALTER COLUMN profile_id TYPE uuid USING profile_id::uuid::uuid;
ALTER TABLE public.profiles
    ALTER COLUMN id TYPE uuid USING id::uuid::uuid;
ALTER TABLE update_profiles
    ADD CONSTRAINT fk_update_profiles_on_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE NO ACTION;