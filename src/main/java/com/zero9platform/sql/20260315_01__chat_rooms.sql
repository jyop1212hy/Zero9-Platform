USE zero9;
CREATE TABLE chat_rooms
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    admin_id          BIGINT       NOT NULL,
    last_message      VARCHAR(255) NOT NULL,
    last_message_time DATETIME     NOT NULL,
    created_at        DATETIME     NOT NULL,

    INDEX idx_room_user (user_id),
    INDEX idx_room_last_message (last_message_time)
);