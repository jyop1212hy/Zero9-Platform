USE zero9;
CREATE TABLE chat_messages
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id     BIGINT   NOT NULL,         -- 추가됨
    sender_id   BIGINT   NOT NULL,
    receiver_id BIGINT   NOT NULL,
    message     TEXT     NOT NULL,
    is_read     BOOLEAN  NOT NULL DEFAULT FALSE, -- 추가됨 (에러의 핵심 원인)
    created_at  DATETIME NOT NULL,

    INDEX       idx_room_time (room_id, created_at),
    INDEX       idx_sender_receiver (sender_id, receiver_id)
);