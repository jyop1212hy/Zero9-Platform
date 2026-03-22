USE zero9;

CREATE TABLE activity_feed
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    type        VARCHAR(50)  NOT NULL,
    target_name VARCHAR(255) NOT NULL,
    target_id   BIGINT NULL,
    user_id     BIGINT NULL,
    CONSTRAINT pk_activity_feed PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    post_id    BIGINT NULL,
    user_id    BIGINT NULL,
    content    TEXT NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE gpp_comments
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    gpp_id     BIGINT NULL,
    user_id    BIGINT NULL,
    content    TEXT NOT NULL,
    CONSTRAINT pk_gpp_comments PRIMARY KEY (id)
);

CREATE TABLE gpp_follows
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    user_id BIGINT NULL,
    gpp_id  BIGINT NULL,
    CONSTRAINT pk_gpp_follows PRIMARY KEY (id)
);

CREATE TABLE group_purchase_posts
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_at          datetime NULL,
    updated_at          datetime NULL,
    user_id             BIGINT       NOT NULL,
    product_name        VARCHAR(255) NOT NULL,
    content             TEXT         NOT NULL,
    image               VARCHAR(255) NULL,
    view_count          BIGINT       NOT NULL,
    price               BIGINT       NOT NULL,
    link_url            VARCHAR(255) NOT NULL,
    category            VARCHAR(255) NOT NULL,
    gpp_progress_status VARCHAR(255) NOT NULL,
    start_date          datetime     NOT NULL,
    end_date            datetime     NOT NULL,
    deleted_at          datetime NULL,
    CONSTRAINT pk_group_purchase_posts PRIMARY KEY (id)
);

CREATE TABLE influencers
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    user_id                    BIGINT       NOT NULL,
    influencer_social_link     VARCHAR(255) NOT NULL,
    influencer_approval_status BIT(1)       NOT NULL,
    approval_at                datetime NULL,
    CONSTRAINT pk_influencers PRIMARY KEY (id)
);

CREATE TABLE keyword_ranking_snapshots
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    keyword       VARCHAR(255) NOT NULL,
    period_type   VARCHAR(255) NOT NULL,
    keyword_count BIGINT       NOT NULL,
    target_date   VARCHAR(255) NOT NULL,
    snapshot_at   datetime     NOT NULL,
    CONSTRAINT pk_keyword_ranking_snapshots PRIMARY KEY (id)
);

CREATE TABLE favorite_ranking_snapshots
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    product_post_id BIGINT       NOT NULL,
    period_type     VARCHAR(255) NOT NULL,
    favorite_count  BIGINT       NOT NULL,
    target_date     VARCHAR(255) NOT NULL,
    snapshot_at     datetime     NOT NULL,
    CONSTRAINT pk_favorite_ranking_snapshots PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_at             datetime NULL,
    updated_at             datetime NULL,
    user_id                BIGINT       NOT NULL,
    group_purchase_post_id BIGINT       NOT NULL,
    title                  VARCHAR(100) NOT NULL,
    content                VARCHAR(255) NOT NULL,
    is_read                BIT(1)       NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_at             datetime NULL,
    updated_at             datetime NULL,
    user_id                BIGINT NOT NULL,
    product_post_id        BIGINT NOT NULL,
    product_post_option_id BIGINT NOT NULL,
    order_quantity         INT    NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime NULL,
    updated_at      datetime NULL,
    order_item_id   BIGINT NULL,
    order_no        VARCHAR(255) NOT NULL,
    total_amount    BIGINT       NOT NULL,
    order_status    VARCHAR(255) NOT NULL,
    canceled_at     datetime NULL,
    canceled_reason VARCHAR(255) NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime NULL,
    updated_at  datetime NULL,
    order_id    BIGINT NULL,
    payment_key VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    user_id    BIGINT NULL,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    is_secret  BIT(1)       NOT NULL,
    password   VARCHAR(255) NULL,
    deleted_at datetime NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE TABLE product_post_options
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime NULL,
    updated_at      datetime NULL,
    product_post_id BIGINT       NOT NULL,
    name            VARCHAR(255) NOT NULL,
    sale_price      BIGINT       NOT NULL,
    stock_quantity  INT          NOT NULL,
    stock_status    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_product_post_options PRIMARY KEY (id)
);

CREATE TABLE product_posts
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime NULL,
    updated_at      datetime NULL,
    user_id         BIGINT       NOT NULL,
    title           VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    original_price  BIGINT       NOT NULL,
    image           VARCHAR(255) NULL,
    category        VARCHAR(255) NOT NULL,
    progress_status VARCHAR(255) NOT NULL,
    start_date      datetime     NOT NULL,
    end_date        datetime     NOT NULL,
    CONSTRAINT pk_product_posts PRIMARY KEY (id)
);

CREATE TABLE product_posts_favorites
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    user_id         BIGINT   NOT NULL,
    product_post_id BIGINT   NOT NULL,
    created_at      datetime NOT NULL,
    CONSTRAINT pk_product_posts_favorites PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    user_id       BIGINT       NOT NULL,
    expire_at     datetime     NOT NULL,
    used          BIT(1)       NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

CREATE TABLE search_logs
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_id    BIGINT NULL,
    keyword    VARCHAR(255) NOT NULL,
    created_at datetime     NOT NULL,
    CONSTRAINT pk_search_logs PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime NULL,
    updated_at    datetime NULL,
    login_id      VARCHAR(100) NULL,
    password      VARCHAR(255) NULL,
    email         VARCHAR(100) NULL,
    name          VARCHAR(50) NULL,
    `role`        VARCHAR(20) NOT NULL,
    phone         VARCHAR(20) NULL,
    nickname      VARCHAR(50) NULL,
    profile_image VARCHAR(255) NULL,
    deleted_at    datetime NULL,
    provider      VARCHAR(20) NOT NULL,
    provider_id   VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE chat_rooms
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    user_id           BIGINT       NOT NULL,
    admin_id          BIGINT       NOT NULL,
    last_message      VARCHAR(255) NOT NULL,
    last_message_time datetime     NOT NULL,
    created_at        datetime     NOT NULL,

    CONSTRAINT pk_chat_rooms PRIMARY KEY (id),

    -- 회원당 문의방 1개
    CONSTRAINT uk_chat_rooms_user UNIQUE (user_id)
);

CREATE TABLE chat_messages
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    room_id     BIGINT       NOT NULL,
    sender_id   BIGINT       NOT NULL,
    receiver_id BIGINT       NOT NULL,
    message     TEXT         NOT NULL,
    is_read     BIT(1)       NOT NULL,
    created_at  datetime     NOT NULL,

    CONSTRAINT pk_chat_messages PRIMARY KEY (id)
);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_order_item UNIQUE (order_item_id);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_orderno UNIQUE (order_no);

ALTER TABLE payments
    ADD CONSTRAINT uc_payments_order UNIQUE (order_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_refreshtoken UNIQUE (refresh_token);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_loginid UNIQUE (login_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_nickname UNIQUE (nickname);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone UNIQUE (phone);

ALTER TABLE keyword_ranking_snapshots
    ADD CONSTRAINT uk_keyword_period_date UNIQUE (keyword, period_type, target_date);

ALTER TABLE favorite_ranking_snapshots
    ADD CONSTRAINT uk_product_period_date UNIQUE (product_post_id, period, target_date);

ALTER TABLE product_posts_favorites
    ADD CONSTRAINT uk_product_post_favorite_user_post UNIQUE (user_id, product_post_id);

CREATE INDEX idx_user_updated ON activity_feed (user_id, updated_at DESC);

CREATE INDEX idx_chat_rooms_admin_last_time
    ON chat_rooms (admin_id, last_message_time DESC);

CREATE INDEX idx_chat_messages_room_time
    ON chat_messages (room_id, created_at ASC);

CREATE INDEX idx_chat_messages_unread
    ON chat_messages (room_id, receiver_id, is_read);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE gpp_comments
    ADD CONSTRAINT FK_GPP_COMMENTS_ON_GPP FOREIGN KEY (gpp_id) REFERENCES group_purchase_posts (id);

ALTER TABLE gpp_comments
    ADD CONSTRAINT FK_GPP_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE gpp_follows
    ADD CONSTRAINT FK_GPP_FOLLOWS_ON_GPP FOREIGN KEY (gpp_id) REFERENCES group_purchase_posts (id);

ALTER TABLE gpp_follows
    ADD CONSTRAINT FK_GPP_FOLLOWS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE group_purchase_posts
    ADD CONSTRAINT FK_GROUP_PURCHASE_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE influencers
    ADD CONSTRAINT FK_INFLUENCERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ORDER_ITEM FOREIGN KEY (order_item_id) REFERENCES order_items (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT_POST FOREIGN KEY (product_post_id) REFERENCES product_posts (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT_POST_OPTION FOREIGN KEY (product_post_option_id) REFERENCES product_post_options (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE posts
    ADD CONSTRAINT FK_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE product_posts_favorites
    ADD CONSTRAINT FK_PRODUCT_POSTS_FAVORITES_ON_PRODUCT_POST FOREIGN KEY (product_post_id) REFERENCES product_posts (id);

ALTER TABLE product_posts_favorites
    ADD CONSTRAINT FK_PRODUCT_POSTS_FAVORITES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE product_posts
    ADD CONSTRAINT FK_PRODUCT_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE product_post_options
    ADD CONSTRAINT FK_PRODUCT_POST_OPTIONS_ON_PRODUCT_POST FOREIGN KEY (product_post_id) REFERENCES product_posts (id);

ALTER TABLE chat_rooms
    ADD CONSTRAINT FK_CHAT_ROOMS_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE chat_rooms
    ADD CONSTRAINT FK_CHAT_ROOMS_ADMIN FOREIGN KEY (admin_id) REFERENCES users (id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ROOM FOREIGN KEY (room_id) REFERENCES chat_rooms (id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_RECEIVER FOREIGN KEY (receiver_id) REFERENCES users (id);