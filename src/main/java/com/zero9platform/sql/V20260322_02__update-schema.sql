USE zero9;

SET @db := DATABASE();

-- =========================================================
-- 1) keyword_ranking_snapshots / favorite_ranking_snapshots
--    unique constraint 보정
-- =========================================================

-- keyword_ranking_snapshots : uk_keyword_period_date
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'keyword_ranking_snapshots'
              AND constraint_name = 'uk_keyword_period_date'
        ),
        'SELECT "uk_keyword_period_date already exists"',
        'ALTER TABLE keyword_ranking_snapshots
            ADD CONSTRAINT uk_keyword_period_date
            UNIQUE (keyword, period_type, target_date)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- favorite_ranking_snapshots : uk_product_period_date
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'favorite_ranking_snapshots'
              AND constraint_name = 'uk_product_period_date'
        ),
        'SELECT "uk_product_period_date already exists"',
        'ALTER TABLE favorite_ranking_snapshots
            ADD CONSTRAINT uk_product_period_date
            UNIQUE (product_post_id, period_type, target_date)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- 2) chat_rooms / chat_messages 신규 테이블
-- =========================================================

CREATE TABLE IF NOT EXISTS chat_rooms
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    user_id           BIGINT       NOT NULL,
    admin_id          BIGINT       NOT NULL,
    last_message      VARCHAR(255) NOT NULL,
    last_message_time datetime     NOT NULL,
    created_at        datetime     NOT NULL,
    CONSTRAINT pk_chat_rooms PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS chat_messages
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

-- =========================================================
-- 3) chat_rooms unique / index
-- =========================================================

-- 회원당 문의방 1개
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_rooms'
              AND constraint_name = 'uk_chat_rooms_user'
        ),
        'SELECT "uk_chat_rooms_user already exists"',
        'ALTER TABLE chat_rooms
            ADD CONSTRAINT uk_chat_rooms_user UNIQUE (user_id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- idx_chat_rooms_admin_last_time
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @db
              AND table_name = 'chat_rooms'
              AND index_name = 'idx_chat_rooms_admin_last_time'
        ),
        'SELECT "idx_chat_rooms_admin_last_time already exists"',
        'CREATE INDEX idx_chat_rooms_admin_last_time
            ON chat_rooms (admin_id, last_message_time DESC)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- 4) chat_messages index
-- =========================================================

-- idx_chat_messages_room_time
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @db
              AND table_name = 'chat_messages'
              AND index_name = 'idx_chat_messages_room_time'
        ),
        'SELECT "idx_chat_messages_room_time already exists"',
        'CREATE INDEX idx_chat_messages_room_time
            ON chat_messages (room_id, created_at ASC)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- idx_chat_messages_unread
SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = @db
              AND table_name = 'chat_messages'
              AND index_name = 'idx_chat_messages_unread'
        ),
        'SELECT "idx_chat_messages_unread already exists"',
        'CREATE INDEX idx_chat_messages_unread
            ON chat_messages (room_id, receiver_id, is_read)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- 5) chat_rooms FK
-- =========================================================

SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_rooms'
              AND constraint_name = 'FK_CHAT_ROOMS_USER'
        ),
        'SELECT "FK_CHAT_ROOMS_USER already exists"',
        'ALTER TABLE chat_rooms
            ADD CONSTRAINT FK_CHAT_ROOMS_USER
            FOREIGN KEY (user_id) REFERENCES users (id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_rooms'
              AND constraint_name = 'FK_CHAT_ROOMS_ADMIN'
        ),
        'SELECT "FK_CHAT_ROOMS_ADMIN already exists"',
        'ALTER TABLE chat_rooms
            ADD CONSTRAINT FK_CHAT_ROOMS_ADMIN
            FOREIGN KEY (admin_id) REFERENCES users (id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- 6) chat_messages FK
-- =========================================================

SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_messages'
              AND constraint_name = 'FK_CHAT_MESSAGES_ROOM'
        ),
        'SELECT "FK_CHAT_MESSAGES_ROOM already exists"',
        'ALTER TABLE chat_messages
            ADD CONSTRAINT FK_CHAT_MESSAGES_ROOM
            FOREIGN KEY (room_id) REFERENCES chat_rooms (id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_messages'
              AND constraint_name = 'FK_CHAT_MESSAGES_SENDER'
        ),
        'SELECT "FK_CHAT_MESSAGES_SENDER already exists"',
        'ALTER TABLE chat_messages
            ADD CONSTRAINT FK_CHAT_MESSAGES_SENDER
            FOREIGN KEY (sender_id) REFERENCES users (id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
        EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = @db
              AND table_name = 'chat_messages'
              AND constraint_name = 'FK_CHAT_MESSAGES_RECEIVER'
        ),
        'SELECT "FK_CHAT_MESSAGES_RECEIVER already exists"',
        'ALTER TABLE chat_messages
            ADD CONSTRAINT FK_CHAT_MESSAGES_RECEIVER
            FOREIGN KEY (receiver_id) REFERENCES users (id)'
            );
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;