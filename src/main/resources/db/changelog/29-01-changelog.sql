-- liquibase formatted sql

-- changeset gravita:1756441183091-1
CREATE SEQUENCE IF NOT EXISTS audit_log_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-2
CREATE SEQUENCE IF NOT EXISTS balance_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-3
CREATE SEQUENCE IF NOT EXISTS balance_transactions_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-4
CREATE SEQUENCE IF NOT EXISTS baninfo_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-5
CREATE SEQUENCE IF NOT EXISTS exchange_rates_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-6
CREATE SEQUENCE IF NOT EXISTS hwids_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-7
CREATE SEQUENCE IF NOT EXISTS item_delivery_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-8
CREATE SEQUENCE IF NOT EXISTS news_comments_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-9
CREATE SEQUENCE IF NOT EXISTS news_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-10
CREATE SEQUENCE IF NOT EXISTS orders_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-11
CREATE SEQUENCE IF NOT EXISTS password_resets_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-12
CREATE SEQUENCE IF NOT EXISTS payments_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-13
CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-14
CREATE SEQUENCE IF NOT EXISTS servers_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-15
CREATE SEQUENCE IF NOT EXISTS sessions_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-16
CREATE SEQUENCE IF NOT EXISTS update_directories_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-17
CREATE SEQUENCE IF NOT EXISTS update_profiles_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-18
CREATE SEQUENCE IF NOT EXISTS user_assets_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-19
CREATE SEQUENCE IF NOT EXISTS user_groups_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-20
CREATE SEQUENCE IF NOT EXISTS user_permissions_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-21
CREATE SEQUENCE IF NOT EXISTS user_rep_change_seq START WITH 1 INCREMENT BY 50;

-- changeset gravita:1756441183091-22
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-23
CREATE TABLE audit_log
(
    id             BIGINT DEFAULT nextval('audit_log_seq') NOT NULL,
    arg1           VARCHAR(255),
    arg2           VARCHAR(255),
    ip             VARCHAR(255),
    time           TIMESTAMP WITHOUT TIME ZONE,
    type           SMALLINT,
    target_user_id BIGINT,
    user_id        BIGINT,
    CONSTRAINT audit_log_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-24
CREATE TABLE balance
(
    id       BIGINT DEFAULT nextval('balance_seq') NOT NULL,
    balance  DOUBLE PRECISION                      NOT NULL,
    currency VARCHAR(255),
    user_id  BIGINT,
    CONSTRAINT balance_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-25
CREATE TABLE balance_transactions
(
    id            BIGINT DEFAULT nextval('balance_transactions_seq') NOT NULL,
    comment       VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    from_count    DOUBLE PRECISION,
    multicurrency BOOLEAN                                            NOT NULL,
    to_count      DOUBLE PRECISION,
    from_id       BIGINT,
    to_id         BIGINT,
    user_id       BIGINT,
    CONSTRAINT balance_transactions_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-26
CREATE TABLE baninfo
(
    id           BIGINT DEFAULT nextval('baninfo_seq') NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    end_at       TIMESTAMP WITHOUT TIME ZONE,
    reason       VARCHAR(255),
    shadow       BOOLEAN                               NOT NULL,
    moderator_id BIGINT,
    target_id    BIGINT,
    CONSTRAINT baninfo_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-27
CREATE TABLE exchange_rates
(
    id            BIGINT DEFAULT nextval('exchange_rates_seq') NOT NULL,
    from_currency VARCHAR(255),
    to_currency   VARCHAR(255),
    value         DOUBLE PRECISION                             NOT NULL,
    CONSTRAINT exchange_rates_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-28
CREATE TABLE group_orders
(
    id         BIGINT DEFAULT nextval('orders_seq') NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    quantity   BIGINT                               NOT NULL,
    status     SMALLINT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id BIGINT,
    user_id    BIGINT,
    server     VARCHAR(255),
    product_id BIGINT,
    CONSTRAINT group_orders_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-29
CREATE TABLE group_products
(
    id           BIGINT DEFAULT nextval('product_seq') NOT NULL,
    available    BOOLEAN                               NOT NULL,
    count        BIGINT                                NOT NULL,
    currency     VARCHAR(255),
    description  VARCHAR(255),
    display_name VARCHAR(255),
    end_data     TIMESTAMP WITHOUT TIME ZONE,
    group_name   VARCHAR(255),
    picture_url  VARCHAR(255),
    price        DOUBLE PRECISION                      NOT NULL,
    context      VARCHAR(255),
    expire_days  BIGINT,
    local        BOOLEAN                               NOT NULL,
    name         VARCHAR(255),
    server       VARCHAR(255),
    stackable    BOOLEAN                               NOT NULL,
    world        VARCHAR(255),
    local_name   VARCHAR(255),
    CONSTRAINT group_products_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-30
CREATE TABLE groups
(
    id           VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    parent_id    VARCHAR(255),
    CONSTRAINT groups_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-31
CREATE TABLE hwids
(
    id                      BIGINT DEFAULT nextval('hwids_seq') NOT NULL,
    banned                  BOOLEAN                             NOT NULL,
    baseboard_serial_number VARCHAR(255),
    battery                 BOOLEAN                             NOT NULL,
    bitness                 INTEGER                             NOT NULL,
    display_id              BYTEA,
    hw_disk_id              VARCHAR(255),
    logical_processors      INTEGER                             NOT NULL,
    physical_processors     INTEGER                             NOT NULL,
    processor_max_freq      BIGINT                              NOT NULL,
    public_key              BYTEA,
    total_memory            BIGINT                              NOT NULL,
    CONSTRAINT hwids_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-32
CREATE TABLE item_delivery
(
    id            BIGINT DEFAULT nextval('item_delivery_seq') NOT NULL,
    completed     BOOLEAN                                     NOT NULL,
    item_enchants VARCHAR(255),
    item_extra    VARCHAR(255),
    item_name     VARCHAR(255),
    item_nbt      TEXT,
    part          BIGINT                                      NOT NULL,
    user_id       BIGINT,
    CONSTRAINT item_delivery_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-33
CREATE TABLE item_orders
(
    id            BIGINT DEFAULT nextval('orders_seq') NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    quantity      BIGINT                               NOT NULL,
    status        SMALLINT,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    payment_id    BIGINT,
    user_id       BIGINT,
    custom_params VARCHAR(255),
    product_id    BIGINT,
    CONSTRAINT item_orders_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-34
CREATE TABLE item_products
(
    id            BIGINT DEFAULT nextval('product_seq') NOT NULL,
    available     BOOLEAN                               NOT NULL,
    count         BIGINT                                NOT NULL,
    currency      VARCHAR(255),
    description   VARCHAR(255),
    display_name  VARCHAR(255),
    end_data      TIMESTAMP WITHOUT TIME ZONE,
    group_name    VARCHAR(255),
    picture_url   VARCHAR(255),
    price         DOUBLE PRECISION                      NOT NULL,
    item_custom   TEXT,
    item_enchants VARCHAR(255),
    item_extra    VARCHAR(255),
    item_name     VARCHAR(255),
    item_nbt      TEXT,
    item_quantity INTEGER,
    server        VARCHAR(255),
    CONSTRAINT item_products_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-35
CREATE TABLE news
(
    id             BIGINT DEFAULT nextval('news_seq') NOT NULL,
    comments_count INTEGER                            NOT NULL,
    header         VARCHAR(255),
    mini_text      TEXT,
    pictureurl     VARCHAR(255),
    text           TEXT,
    CONSTRAINT news_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-36
CREATE TABLE news_comments
(
    id      BIGINT DEFAULT nextval('news_comments_seq') NOT NULL,
    text    TEXT,
    news_id BIGINT,
    user_id BIGINT,
    CONSTRAINT news_comments_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-37
CREATE TABLE password_resets
(
    id      BIGINT DEFAULT nextval('password_resets_seq') NOT NULL,
    uuid    CHAR(36),
    user_id BIGINT,
    CONSTRAINT password_resets_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-38
CREATE TABLE payments
(
    id                BIGINT DEFAULT nextval('payments_seq') NOT NULL,
    status            SMALLINT,
    sum               DOUBLE PRECISION                       NOT NULL,
    system            VARCHAR(255),
    system_payment_id VARCHAR(255),
    user_id           BIGINT,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-39
CREATE TABLE prepare_users
(
    id            BIGINT NOT NULL,
    confirm_token VARCHAR(255),
    date          TIMESTAMP WITHOUT TIME ZONE,
    email         VARCHAR(255),
    hash_type     SMALLINT,
    password      VARCHAR(255),
    username      VARCHAR(255),
    CONSTRAINT prepare_users_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-40
CREATE TABLE profiles
(
    id               CHAR(36) NOT NULL,
    name             VARCHAR(255),
    description      VARCHAR(65535),
    icon_id          VARCHAR(255),
    picture_id       VARCHAR(255),
    large_picture_id VARCHAR(255),
    limited          BOOLEAN  NOT NULL,
    tag              VARCHAR(255),
    CONSTRAINT pk_profiles PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-41
CREATE TABLE servers
(
    id           BIGINT DEFAULT nextval('servers_seq') NOT NULL,
    display_name VARCHAR(255),
    max_online   INTEGER,
    name         VARCHAR(255),
    online       INTEGER                               NOT NULL,
    tps          INTEGER                               NOT NULL,
    update_date  TIMESTAMP WITHOUT TIME ZONE,
    users        VARCHAR(255)[],
    CONSTRAINT servers_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-42
CREATE TABLE service_orders
(
    id         BIGINT DEFAULT nextval('orders_seq') NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    quantity   BIGINT                               NOT NULL,
    status     SMALLINT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id BIGINT,
    user_id    BIGINT,
    product_id BIGINT,
    CONSTRAINT service_orders_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-43
CREATE TABLE service_products
(
    id           BIGINT DEFAULT nextval('product_seq') NOT NULL,
    available    BOOLEAN                               NOT NULL,
    count        BIGINT                                NOT NULL,
    currency     VARCHAR(255),
    description  VARCHAR(255),
    display_name VARCHAR(255),
    end_data     TIMESTAMP WITHOUT TIME ZONE,
    group_name   VARCHAR(255),
    picture_url  VARCHAR(255),
    price        DOUBLE PRECISION                      NOT NULL,
    days         INTEGER                               NOT NULL,
    stackable    BOOLEAN                               NOT NULL,
    type         SMALLINT,
    CONSTRAINT service_products_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-44
CREATE TABLE sessions
(
    id            BIGINT DEFAULT nextval('sessions_seq') NOT NULL,
    client        VARCHAR(255),
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    deleted       BOOLEAN                                NOT NULL,
    ip            INET,
    refresh_token VARCHAR(255),
    server_id     VARCHAR(255),
    hwid_id       BIGINT,
    user_id       BIGINT,
    CONSTRAINT sessions_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-45
CREATE TABLE update_directories
(
    id      BIGINT NOT NULL,
    content JSONB,
    CONSTRAINT pk_update_directories PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-46
CREATE TABLE update_profiles
(
    id               BIGINT NOT NULL,
    profile_id       CHAR(36),
    client_update_id BIGINT,
    asset_update_id  BIGINT,
    content          JSONB,
    previous_id      BIGINT,
    tag              VARCHAR(255),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_update_profiles PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-47
CREATE TABLE user_assets
(
    id         BIGINT DEFAULT nextval('user_assets_seq') NOT NULL,
    user_id    BIGINT,
    type       VARCHAR(31)                               NOT NULL,
    digest     VARCHAR(64)                               NOT NULL,
    file_name  VARCHAR(255)                              NOT NULL,
    file_size  BIGINT                                    NOT NULL,
    width      INTEGER,
    height     INTEGER,
    metadata   TEXT                                      DEFAULT '{}',
    uploaded_at TIMESTAMP WITHOUT TIME ZONE              NOT NULL,
    last_accessed TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT user_assets_pkey PRIMARY KEY (id),
    CONSTRAINT user_assets_digest_unique UNIQUE (digest)
);

-- changeset gravita:1756441183091-48
CREATE TABLE user_groups
(
    id         BIGINT DEFAULT nextval('user_groups_seq') NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    group_id   VARCHAR(255),
    user_id    BIGINT,
    CONSTRAINT user_groups_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-49
CREATE TABLE user_permissions
(
    id       BIGINT DEFAULT nextval('user_permissions_seq') NOT NULL,
    name     VARCHAR(255),
    value    VARCHAR(255),
    group_id VARCHAR(255),
    CONSTRAINT user_permissions_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-50
CREATE TABLE user_rep_change
(
    id        BIGINT DEFAULT nextval('user_rep_change_seq') NOT NULL,
    date      TIMESTAMP WITHOUT TIME ZONE,
    reason    SMALLINT,
    value     BIGINT,
    target_id BIGINT,
    user_id   BIGINT,
    CONSTRAINT user_rep_change_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-51
CREATE TABLE users
(
    id                BIGINT DEFAULT nextval('users_seq') NOT NULL,
    email             VARCHAR(255),
    gender            SMALLINT,
    hash_type         SMALLINT,
    password          VARCHAR(255),
    prefix            VARCHAR(255),
    registration_date TIMESTAMP WITHOUT TIME ZONE,
    reputation        BIGINT,
    status            VARCHAR(255),
    totp_secret_key   VARCHAR(255),
    username          VARCHAR(255),
    uuid              CHAR(36),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- changeset gravita:1756441183091-52
ALTER TABLE users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

-- changeset gravita:1756441183091-53
ALTER TABLE users
    ADD CONSTRAINT uk6km2m9i3vjuy36rnvkgj1l61s UNIQUE (uuid);

-- changeset gravita:1756441183091-54
ALTER TABLE users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);

-- changeset gravita:1756441183091-55
ALTER TABLE prepare_users
    ADD CONSTRAINT prepare_users_username_unique UNIQUE (username);

-- changeset gravita:1756441183091-56
ALTER TABLE prepare_users
    ADD CONSTRAINT prepare_users_email_unique UNIQUE (email);

-- changeset gravita:1756441183091-57
CREATE UNIQUE INDEX prepare_users_confirm_token_idx ON prepare_users (confirm_token);

-- changeset gravita:1756441183091-58
CREATE UNIQUE INDEX servers_name_idx ON servers (name);

-- changeset gravita:1756441183091-59
CREATE INDEX sessions_refresh_token_idx ON sessions (refresh_token);

-- changeset gravita:1756441183091-60
CREATE INDEX sessions_server_id_idx ON sessions (server_id);

-- changeset gravita:1756441183091-61
ALTER TABLE item_orders
    ADD CONSTRAINT fk3sj3845ev373hy06lio15yc40 FOREIGN KEY (product_id) REFERENCES item_products (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-62
ALTER TABLE group_products
    ADD CONSTRAINT fk4sm9e9n376j1fu3jg99fath5s FOREIGN KEY (local_name) REFERENCES groups (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-63
ALTER TABLE user_assets
    ADD CONSTRAINT fk699abv0gu9pantv0sresry5qs FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-64
ALTER TABLE item_orders
    ADD CONSTRAINT fk6te8gxaeti0w8vupxaianftuu FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-65
ALTER TABLE user_permissions
    ADD CONSTRAINT fk7icaohwwhy6sgib14r4yd6tfn FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-66
ALTER TABLE balance_transactions
    ADD CONSTRAINT fk7o6jeohmkettv3tg2vsfmv0b0 FOREIGN KEY (from_id) REFERENCES balance (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-67
ALTER TABLE service_orders
    ADD CONSTRAINT fk8cniilwik3j9a3njtk15n9wuo FOREIGN KEY (product_id) REFERENCES service_products (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-68
ALTER TABLE audit_log
    ADD CONSTRAINT fk901ojla9lodhsnxpoal0u65ql FOREIGN KEY (target_user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-69
ALTER TABLE update_profiles
    ADD CONSTRAINT fk_update_profiles_on_asset_update FOREIGN KEY (asset_update_id) REFERENCES update_directories (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-70
ALTER TABLE update_profiles
    ADD CONSTRAINT fk_update_profiles_on_client_update FOREIGN KEY (client_update_id) REFERENCES update_directories (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-71
ALTER TABLE update_profiles
    ADD CONSTRAINT fk_update_profiles_on_previous FOREIGN KEY (previous_id) REFERENCES update_profiles (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-72
ALTER TABLE update_profiles
    ADD CONSTRAINT fk_update_profiles_on_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-73
ALTER TABLE group_orders
    ADD CONSTRAINT fkaqdg201ornq5g69o3ad0q8tyo FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-74
ALTER TABLE news_comments
    ADD CONSTRAINT fkb3m8xh8vkopvlsp3f05njymrd FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-75
ALTER TABLE baninfo
    ADD CONSTRAINT fkbqr5huwuyx9vqw0nrd4ap97x0 FOREIGN KEY (moderator_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-76
ALTER TABLE user_groups
    ADD CONSTRAINT fkd37bs5u9hvbwljup24b2hin2b FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-77
ALTER TABLE balance_transactions
    ADD CONSTRAINT fkdgk044yu1nxp7bwyotd4oj0cx FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-78
ALTER TABLE balance_transactions
    ADD CONSTRAINT fkeldgti5mwlmkka845txsxcy0i FOREIGN KEY (to_id) REFERENCES balance (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-79
ALTER TABLE groups
    ADD CONSTRAINT fkfvqfb4l9r8hbfhfm515o77esh FOREIGN KEY (parent_id) REFERENCES groups (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-80
ALTER TABLE password_resets
    ADD CONSTRAINT fkfy4ulhbvy3yguwnqqvts2iqqx FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-81
ALTER TABLE item_delivery
    ADD CONSTRAINT fkge5bqxiv6su91g2hsimgvrr7o FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-82
ALTER TABLE sessions
    ADD CONSTRAINT fkghktmryar87ihcde9ynmv1rgc FOREIGN KEY (hwid_id) REFERENCES hwids (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-83
ALTER TABLE service_orders
    ADD CONSTRAINT fkhumng94k11eeh2fkquoai7qbb FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-84
ALTER TABLE news_comments
    ADD CONSTRAINT fkhv8k5odywchi3oodslspm2fgy FOREIGN KEY (news_id) REFERENCES news (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-85
ALTER TABLE payments
    ADD CONSTRAINT fkj94hgy9v5fw1munb90tar2eje FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-86
ALTER TABLE audit_log
    ADD CONSTRAINT fkk4alalwu62gj4tfbgfefll3tu FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-87
ALTER TABLE item_orders
    ADD CONSTRAINT fkm65n5pch15g1iuw8xejwwm831 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-88
ALTER TABLE group_orders
    ADD CONSTRAINT fkmfj7cqw1iv1bln7kmcw0tg2w8 FOREIGN KEY (product_id) REFERENCES group_products (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-89
ALTER TABLE service_orders
    ADD CONSTRAINT fkmhertkwtt396qiwydf15kn1h7 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-90
ALTER TABLE user_groups
    ADD CONSTRAINT fkmrgahbb4w32n9wkjqbipttc87 FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-91
ALTER TABLE user_rep_change
    ADD CONSTRAINT fknhsunh7iy0jooe36sw1nypi33 FOREIGN KEY (target_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-92
ALTER TABLE baninfo
    ADD CONSTRAINT fkoo06lt2vh2gwih9h8b89eshuo FOREIGN KEY (target_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-93
ALTER TABLE user_rep_change
    ADD CONSTRAINT fkordv327gvtdhgnj865dr48p3q FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-94
ALTER TABLE group_orders
    ADD CONSTRAINT fkqqdhp9wnlf16epno5ub4rhrwc FOREIGN KEY (payment_id) REFERENCES payments (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-95
ALTER TABLE sessions
    ADD CONSTRAINT fkruie73rneumyyd1bgo6qw8vjt FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-96
ALTER TABLE balance
    ADD CONSTRAINT fksdu7qx7cs4vxvi8rf9bgrwrb4 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-97
ALTER TABLE service_orders
    ADD CONSTRAINT fk_service_orders_product FOREIGN KEY (product_id) REFERENCES service_products (id) ON DELETE NO ACTION;

-- changeset gravita:1756441183091-98
CREATE INDEX payments_user_id_idx ON payments (user_id);

-- changeset gravita:1756441183091-99
CREATE INDEX sessions_user_id_idx ON sessions (user_id);

-- changeset gravita:1756441183091-100
ALTER TABLE users ALTER COLUMN uuid TYPE uuid USING uuid::uuid;

-- changeset gravita:1756441183091-101
CREATE SEQUENCE IF NOT EXISTS prepare_users_seq START WITH 1 INCREMENT BY 1;

-- changeset gravita:1756441183091-102
CREATE INDEX user_assets_user_id_idx ON user_assets (user_id);

-- changeset gravita:1756441183091-103
CREATE INDEX user_assets_type_idx ON user_assets (type);

-- changeset gravita:1756441183091-104
ALTER TABLE user_assets
    ADD CONSTRAINT chk_user_assets_type CHECK (type IN ('SKIN', 'CAPE', 'AVATAR'));

-- changeset gravita:1756441183091-105
ALTER TABLE prepare_users
    ALTER COLUMN id SET DEFAULT nextval('prepare_users_seq');

-- changeset gravita:1756441183091-106
ALTER TABLE prepare_users
    ADD CONSTRAINT uk_prepare_users_username UNIQUE (username);

-- changeset gravita:1756441183091-107
ALTER TABLE prepare_users
    ADD CONSTRAINT uk_prepare_users_email UNIQUE (email);

-- changeset gravita:1756441183091-108
ALTER TABLE password_resets
    ALTER COLUMN uuid
    TYPE uuid
    USING uuid::uuid;