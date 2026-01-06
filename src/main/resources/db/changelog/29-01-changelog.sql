-- changeset lolens:1756441183091-1
CREATE SEQUENCE IF NOT EXISTS audit_log_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-2
CREATE SEQUENCE IF NOT EXISTS balance_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-3
CREATE SEQUENCE IF NOT EXISTS balance_transactions_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-4
CREATE SEQUENCE IF NOT EXISTS baninfo_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-5
CREATE SEQUENCE IF NOT EXISTS exchange_rates_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-6
CREATE SEQUENCE IF NOT EXISTS hwids_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-7
CREATE SEQUENCE IF NOT EXISTS item_delivery_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-8
CREATE SEQUENCE IF NOT EXISTS news_comments_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-9
CREATE SEQUENCE IF NOT EXISTS news_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-10
CREATE SEQUENCE IF NOT EXISTS orders_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-11
CREATE SEQUENCE IF NOT EXISTS password_resets_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-12
CREATE SEQUENCE IF NOT EXISTS payments_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-13
CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-14
CREATE SEQUENCE IF NOT EXISTS servers_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-15
CREATE SEQUENCE IF NOT EXISTS sessions_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-16
CREATE SEQUENCE IF NOT EXISTS update_directories_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-17
CREATE SEQUENCE IF NOT EXISTS update_profiles_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-18
CREATE SEQUENCE IF NOT EXISTS user_assets_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-19
CREATE SEQUENCE IF NOT EXISTS user_groups_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-20
CREATE SEQUENCE IF NOT EXISTS user_permissions_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-21
CREATE SEQUENCE IF NOT EXISTS user_rep_change_seq START WITH 1 INCREMENT BY 50;

-- changeset lolens:1756441183091-22
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-23
CREATE SEQUENCE IF NOT EXISTS prepare_users_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-24
CREATE SEQUENCE IF NOT EXISTS users_rep_change_seq START WITH 1 INCREMENT BY 1;

-- changeset lolens:1756441183091-25
CREATE TABLE audit_log (
    id BIGINT DEFAULT nextval('audit_log_seq') NOT NULL PRIMARY KEY,
    arg1 VARCHAR(255),
    arg2 VARCHAR(255),
    ip VARCHAR(255),
    time TIMESTAMP WITHOUT TIME ZONE,
    type SMALLINT,
    target_user_id BIGINT,
    user_id BIGINT
);

-- changeset lolens:1756441183091-26
CREATE TABLE users (
    id BIGINT DEFAULT nextval('users_seq') NOT NULL PRIMARY KEY,
    email VARCHAR(255),
    gender SMALLINT,
    hash_type SMALLINT,
    password VARCHAR(255),
    prefix VARCHAR(255),
    registration_date TIMESTAMP WITHOUT TIME ZONE,
    reputation BIGINT,
    status VARCHAR(255),
    totp_secret_key VARCHAR(255),
    username VARCHAR(255),
    uuid UUID,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_uuid UNIQUE (uuid),
    CONSTRAINT uk_users_username UNIQUE (username)
);

-- changeset lolens:1756441183091-27
CREATE TABLE balance (
    id BIGINT DEFAULT nextval('balance_seq') NOT NULL PRIMARY KEY,
    balance DOUBLE PRECISION NOT NULL,
    currency VARCHAR(255),
    user_id BIGINT
);

-- changeset lolens:1756441183091-28
CREATE TABLE balance_transactions (
    id BIGINT DEFAULT nextval('balance_transactions_seq') NOT NULL PRIMARY KEY,
    comment VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    from_count DOUBLE PRECISION,
    multicurrency BOOLEAN NOT NULL,
    to_count DOUBLE PRECISION,
    from_id BIGINT,
    to_id BIGINT,
    user_id BIGINT
);

-- changeset lolens:1756441183091-29
CREATE TABLE baninfo (
    id BIGINT DEFAULT nextval('baninfo_seq') NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    end_at TIMESTAMP WITHOUT TIME ZONE,
    reason VARCHAR(255),
    shadow BOOLEAN NOT NULL,
    moderator_id BIGINT,
    target_id BIGINT
);

-- changeset lolens:1756441183091-30
CREATE TABLE exchange_rates (
    id BIGINT DEFAULT nextval('exchange_rates_seq') NOT NULL PRIMARY KEY,
    from_currency VARCHAR(255),
    to_currency VARCHAR(255),
    value DOUBLE PRECISION NOT NULL
);

-- changeset lolens:1756441183091-31
CREATE TABLE groups (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    display_name VARCHAR(255),
    parent_id VARCHAR(255)
);

-- changeset lolens:1756441183091-32
CREATE TABLE hwids (
    id BIGINT DEFAULT nextval('hwids_seq') NOT NULL PRIMARY KEY,
    banned BOOLEAN NOT NULL,
    baseboard_serial_number VARCHAR(255),
    battery BOOLEAN NOT NULL,
    bitness INTEGER NOT NULL,
    display_id BYTEA,
    hw_disk_id VARCHAR(255),
    logical_processors INTEGER NOT NULL,
    physical_processors INTEGER NOT NULL,
    processor_max_freq BIGINT NOT NULL,
    public_key BYTEA,
    total_memory BIGINT NOT NULL
);

-- changeset lolens:1756441183091-33
CREATE TABLE item_delivery (
    id BIGINT DEFAULT nextval('item_delivery_seq') NOT NULL PRIMARY KEY,
    completed BOOLEAN NOT NULL,
    item_enchants VARCHAR(255),
    item_extra VARCHAR(255),
    item_name VARCHAR(255),
    item_nbt TEXT,
    part BIGINT NOT NULL,
    user_id BIGINT
);

-- changeset lolens:1756441183091-34
CREATE TABLE news (
    id BIGINT DEFAULT nextval('news_seq') NOT NULL PRIMARY KEY,
    comments_count INTEGER NOT NULL,
    header VARCHAR(255),
    mini_text TEXT,
    pictureurl VARCHAR(255),
    text TEXT
);

-- changeset lolens:1756441183091-35
CREATE TABLE news_comments (
    id BIGINT DEFAULT nextval('news_comments_seq') NOT NULL PRIMARY KEY,
    text TEXT,
    news_id BIGINT,
    user_id BIGINT
);

-- changeset lolens:1756441183091-36
CREATE TABLE password_resets (
    id BIGINT DEFAULT nextval('password_resets_seq') NOT NULL PRIMARY KEY,
    uuid UUID,
    user_id BIGINT
);

-- changeset lolens:1756441183091-37
CREATE TABLE payments (
    id BIGINT DEFAULT nextval('payments_seq') NOT NULL PRIMARY KEY,
    status SMALLINT,
    sum DOUBLE PRECISION NOT NULL,
    system VARCHAR(255),
    system_payment_id VARCHAR(255),
    user_id BIGINT
);

-- changeset lolens:1756441183091-38
CREATE TABLE prepare_users (
    id BIGINT DEFAULT nextval('prepare_users_seq') NOT NULL PRIMARY KEY,
    confirm_token VARCHAR(255),
    date TIMESTAMP WITHOUT TIME ZONE,
    email VARCHAR(255),
    hash_type SMALLINT,
    password VARCHAR(255),
    username VARCHAR(255),
    CONSTRAINT uk_prepare_users_username UNIQUE (username),
    CONSTRAINT uk_prepare_users_email UNIQUE (email)
);

-- changeset lolens:1756441183091-39
CREATE TABLE profiles (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(65535),
    icon_id VARCHAR(255),
    picture_id VARCHAR(255),
    large_picture_id VARCHAR(255),
    limited BOOLEAN NOT NULL,
    tag VARCHAR(255)
);

-- changeset lolens:1756441183091-40
CREATE TABLE servers (
    id BIGINT DEFAULT nextval('servers_seq') NOT NULL PRIMARY KEY,
    display_name VARCHAR(255),
    max_online INTEGER,
    name VARCHAR(255),
    online INTEGER NOT NULL,
    tps INTEGER NOT NULL,
    update_date TIMESTAMP WITHOUT TIME ZONE,
    users VARCHAR(255)[]
);

-- changeset lolens:1756441183091-41
CREATE TABLE update_directories (
    id BIGINT NOT NULL PRIMARY KEY,
    content JSONB
);

-- changeset lolens:1756441183091-42
CREATE TABLE update_profiles (
    id BIGINT NOT NULL PRIMARY KEY,
    profile_id UUID,
    client_update_id BIGINT,
    asset_update_id BIGINT,
    content JSONB,
    previous_id BIGINT,
    tag VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset lolens:1756441183091-43
CREATE TABLE user_assets (
    id BIGINT DEFAULT nextval('user_assets_seq') NOT NULL PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(31) NOT NULL,
    digest VARCHAR(64) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    width INTEGER,
    height INTEGER,
    metadata JSONB,
    uploaded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_accessed TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT user_assets_digest_unique UNIQUE (digest),
    CONSTRAINT chk_user_assets_type CHECK (type IN ('SKIN', 'CAPE', 'AVATAR'))
);

-- changeset lolens:1756441183091-44
CREATE TABLE user_groups (
    id BIGINT DEFAULT nextval('user_groups_seq') NOT NULL PRIMARY KEY,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    group_id VARCHAR(255),
    user_id BIGINT
);

-- changeset lolens:1756441183091-45
CREATE TABLE user_permissions (
    id BIGINT DEFAULT nextval('user_permissions_seq') NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    value VARCHAR(255),
    group_id VARCHAR(255)
);

-- changeset lolens:1756441183091-46
CREATE TABLE user_rep_change (
    id BIGINT DEFAULT nextval('user_rep_change_seq') NOT NULL PRIMARY KEY,
    date TIMESTAMP WITHOUT TIME ZONE,
    reason SMALLINT,
    value BIGINT,
    target_id BIGINT,
    user_id BIGINT
);

-- changeset lolens:1756441183091-47
CREATE TABLE group_products (
    id BIGINT DEFAULT nextval('product_seq') NOT NULL PRIMARY KEY,
    available BOOLEAN NOT NULL,
    count BIGINT NOT NULL,
    currency VARCHAR(255),
    description VARCHAR(255),
    display_name VARCHAR(255),
    end_data TIMESTAMP WITHOUT TIME ZONE,
    group_name VARCHAR(255),
    picture_url VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    context VARCHAR(255),
    expire_days BIGINT,
    local BOOLEAN NOT NULL,
    name VARCHAR(255),
    server VARCHAR(255),
    stackable BOOLEAN NOT NULL,
    world VARCHAR(255),
    local_name VARCHAR(255)
);

-- changeset lolens:1756441183091-48
CREATE TABLE item_products (
    id BIGINT DEFAULT nextval('product_seq') NOT NULL PRIMARY KEY,
    available BOOLEAN NOT NULL,
    count BIGINT NOT NULL,
    currency VARCHAR(255),
    description VARCHAR(255),
    display_name VARCHAR(255),
    end_data TIMESTAMP WITHOUT TIME ZONE,
    group_name VARCHAR(255),
    picture_url VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    item_custom TEXT,
    item_enchants VARCHAR(255),
    item_extra VARCHAR(255),
    item_name VARCHAR(255),
    item_nbt TEXT,
    item_quantity INTEGER,
    server VARCHAR(255)
);

-- changeset lolens:1756441183091-49
CREATE TABLE service_products (
    id BIGINT DEFAULT nextval('product_seq') NOT NULL PRIMARY KEY,
    available BOOLEAN NOT NULL,
    count BIGINT NOT NULL,
    currency VARCHAR(255),
    description VARCHAR(255),
    display_name VARCHAR(255),
    end_data TIMESTAMP WITHOUT TIME ZONE,
    group_name VARCHAR(255),
    picture_url VARCHAR(255),
    price DOUBLE PRECISION NOT NULL,
    days INTEGER NOT NULL,
    stackable BOOLEAN NOT NULL,
    type SMALLINT
);

-- changeset lolens:1756441183091-50
CREATE TABLE group_orders (
    id BIGINT DEFAULT nextval('orders_seq') NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    quantity BIGINT NOT NULL,
    status SMALLINT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id BIGINT,
    user_id BIGINT,
    server VARCHAR(255),
    product_id BIGINT
);

-- changeset lolens:1756441183091-51
CREATE TABLE item_orders (
    id BIGINT DEFAULT nextval('orders_seq') NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    quantity BIGINT NOT NULL,
    status SMALLINT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id BIGINT,
    user_id BIGINT,
    custom_params VARCHAR(255),
    product_id BIGINT
);

-- changeset lolens:1756441183091-52
CREATE TABLE service_orders (
    id BIGINT DEFAULT nextval('orders_seq') NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    quantity BIGINT NOT NULL,
    status SMALLINT,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    payment_id BIGINT,
    user_id BIGINT,
    product_id BIGINT
);

-- changeset lolens:1756441183091-53
CREATE TABLE sessions (
    id BIGINT DEFAULT nextval('sessions_seq') NOT NULL PRIMARY KEY,
    client VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    deleted BOOLEAN NOT NULL,
    ip INET,
    refresh_token VARCHAR(255),
    server_id VARCHAR(255),
    hwid_id BIGINT,
    user_id BIGINT
);

-- changeset lolens:1756441183091-54
CREATE UNIQUE INDEX prepare_users_confirm_token_idx ON prepare_users (confirm_token);

-- changeset lolens:1756441183091-55
CREATE UNIQUE INDEX servers_name_idx ON servers (name);

-- changeset lolens:1756441183091-56
CREATE INDEX sessions_refresh_token_idx ON sessions (refresh_token);

-- changeset lolens:1756441183091-57
CREATE INDEX sessions_server_id_idx ON sessions (server_id);

-- changeset lolens:1756441183091-58
CREATE INDEX payments_user_id_idx ON payments (user_id);

-- changeset lolens:1756441183091-59
CREATE INDEX sessions_user_id_idx ON sessions (user_id);

-- changeset lolens:1756441183091-60
CREATE INDEX user_assets_user_id_idx ON user_assets (user_id);

-- changeset lolens:1756441183091-61
CREATE INDEX user_assets_type_idx ON user_assets (type);

-- changeset lolens:1756441183091-62
ALTER TABLE groups ADD CONSTRAINT fk_groups_parent FOREIGN KEY (parent_id) REFERENCES groups (id);

-- changeset lolens:1756441183091-63
ALTER TABLE audit_log ADD CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-64
ALTER TABLE audit_log ADD CONSTRAINT fk_audit_log_target_user FOREIGN KEY (target_user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-65
ALTER TABLE balance ADD CONSTRAINT fk_balance_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-66
ALTER TABLE balance_transactions ADD CONSTRAINT fk_balance_transactions_from FOREIGN KEY (from_id) REFERENCES balance (id);

-- changeset lolens:1756441183091-67
ALTER TABLE balance_transactions ADD CONSTRAINT fk_balance_transactions_to FOREIGN KEY (to_id) REFERENCES balance (id);

-- changeset lolens:1756441183091-68
ALTER TABLE balance_transactions ADD CONSTRAINT fk_balance_transactions_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-69
ALTER TABLE baninfo ADD CONSTRAINT fk_baninfo_moderator FOREIGN KEY (moderator_id) REFERENCES users (id);

-- changeset lolens:1756441183091-70
ALTER TABLE baninfo ADD CONSTRAINT fk_baninfo_target FOREIGN KEY (target_id) REFERENCES users (id);

-- changeset lolens:1756441183091-71
ALTER TABLE group_products ADD CONSTRAINT fk_group_products_local_name FOREIGN KEY (local_name) REFERENCES groups (id);

-- changeset lolens:1756441183091-72
ALTER TABLE item_delivery ADD CONSTRAINT fk_item_delivery_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-73
ALTER TABLE news_comments ADD CONSTRAINT fk_news_comments_news FOREIGN KEY (news_id) REFERENCES news (id);

-- changeset lolens:1756441183091-74
ALTER TABLE news_comments ADD CONSTRAINT fk_news_comments_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-75
ALTER TABLE password_resets ADD CONSTRAINT fk_password_resets_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-76
ALTER TABLE payments ADD CONSTRAINT fk_payments_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-77
ALTER TABLE sessions ADD CONSTRAINT fk_sessions_hwid FOREIGN KEY (hwid_id) REFERENCES hwids (id);

-- changeset lolens:1756441183091-78
ALTER TABLE sessions ADD CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-79
ALTER TABLE update_profiles ADD CONSTRAINT fk_update_profiles_profile FOREIGN KEY (profile_id) REFERENCES profiles (id);

-- changeset lolens:1756441183091-80
ALTER TABLE update_profiles ADD CONSTRAINT fk_update_profiles_client_update FOREIGN KEY (client_update_id) REFERENCES update_directories (id);

-- changeset lolens:1756441183091-81
ALTER TABLE update_profiles ADD CONSTRAINT fk_update_profiles_asset_update FOREIGN KEY (asset_update_id) REFERENCES update_directories (id);

-- changeset lolens:1756441183091-82
ALTER TABLE update_profiles ADD CONSTRAINT fk_update_profiles_previous FOREIGN KEY (previous_id) REFERENCES update_profiles (id);

-- changeset lolens:1756441183091-83
ALTER TABLE user_assets ADD CONSTRAINT fk_user_assets_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-84
ALTER TABLE user_groups ADD CONSTRAINT fk_user_groups_group FOREIGN KEY (group_id) REFERENCES groups (id);

-- changeset lolens:1756441183091-85
ALTER TABLE user_groups ADD CONSTRAINT fk_user_groups_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-86
ALTER TABLE user_permissions ADD CONSTRAINT fk_user_permissions_group FOREIGN KEY (group_id) REFERENCES groups (id);

-- changeset lolens:1756441183091-87
ALTER TABLE user_rep_change ADD CONSTRAINT fk_user_rep_change_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-88
ALTER TABLE user_rep_change ADD CONSTRAINT fk_user_rep_change_target FOREIGN KEY (target_id) REFERENCES users (id);

-- changeset lolens:1756441183091-89
ALTER TABLE group_orders ADD CONSTRAINT fk_group_orders_payment FOREIGN KEY (payment_id) REFERENCES payments (id);

-- changeset lolens:1756441183091-90
ALTER TABLE group_orders ADD CONSTRAINT fk_group_orders_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-91
ALTER TABLE group_orders ADD CONSTRAINT fk_group_orders_product FOREIGN KEY (product_id) REFERENCES group_products (id);

-- changeset lolens:1756441183091-92
ALTER TABLE item_orders ADD CONSTRAINT fk_item_orders_payment FOREIGN KEY (payment_id) REFERENCES payments (id);

-- changeset lolens:1756441183091-93
ALTER TABLE item_orders ADD CONSTRAINT fk_item_orders_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-94
ALTER TABLE item_orders ADD CONSTRAINT fk_item_orders_product FOREIGN KEY (product_id) REFERENCES item_products (id);

-- changeset lolens:1756441183091-95
ALTER TABLE service_orders ADD CONSTRAINT fk_service_orders_payment FOREIGN KEY (payment_id) REFERENCES payments (id);

-- changeset lolens:1756441183091-96
ALTER TABLE service_orders ADD CONSTRAINT fk_service_orders_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset lolens:1756441183091-97
ALTER TABLE service_orders ADD CONSTRAINT fk_service_orders_product FOREIGN KEY (product_id) REFERENCES service_products (id);


